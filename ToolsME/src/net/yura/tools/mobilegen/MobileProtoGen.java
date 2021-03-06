package net.yura.tools.mobilegen;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import net.yura.tools.mobilegen.ProtoLoader.EnumDefinition;
import net.yura.tools.mobilegen.ProtoLoader.FieldDefinition;
import net.yura.tools.mobilegen.ProtoLoader.MessageDefinition;

/**
 * @author Yura Mamyrin
 */
public class MobileProtoGen extends BaseGen {

    public boolean obfuscate = false;
    public boolean split = false;
    public boolean publicEnums = false;
    public boolean skipDeprecated = false;
    
    public static String compute="compute";
    public static String encode="encode";
    public static String decode="decode";
    public static String size="Size";
    
    String protoSource    = null;
    String[] objectPackage = new String[0];
    String extendClass = "ProtoUtil";

    public void setProtoSource( String argument ) {
	    this.protoSource   = argument;
    }
    public void setObjectPackage( String argument ) {
        if (argument==null) {
            objectPackage = new String[0];
        }
        else {
            objectPackage = argument.split("\\,");
        }
    }
    public void setExtendClass(String cl) {
        extendClass = cl;
    }
    
    public void setObfuscate(boolean o) {
        obfuscate = o;
        
        if (obfuscate) {
            compute="c"; // compute
            encode="e"; // encode
            decode="d"; // decode
            size=""; // Size
        }
    }
    
    public void setSplit(boolean o) {
        split = o;
    }
    public void setSkipDeprecated(boolean o) {
        skipDeprecated = o;
    }
    
    public void setPublicEnums(boolean o) {
        publicEnums = o;
    }

    private boolean use(Object obj) {
        if (obj instanceof MessageDefinition) {
            return !skipDeprecated || !((MessageDefinition)obj).isDeprecated();
        }
        else if (obj instanceof EnumDefinition) {
            return !skipDeprecated || !((EnumDefinition)obj).isDeprecated();
        }
        else if (obj instanceof FieldDefinition) {
            return !skipDeprecated || !((FieldDefinition)obj).isDeprecated();
        }
        else {
            throw new RuntimeException();
        }
    }
    
    @Override
    public void doGen() throws Exception {




ProtoLoader loader = new ProtoLoader();
loader.process(protoSource, objectPackage);

enumDefs = loader.getEnumDefs();
messageDefs = loader.getMessageDefs();




        File outFile = getGeneratedFile();

        PrintStream ps = printClass( outFile , getOutputClass(), split? getOutputClass()+"Decode" :extendClass);

        if (split) {
            PrintStream ps1 = printClass( new File(outFile.getParentFile(),outFile.getName().substring(0, outFile.getName().lastIndexOf('.'))+"Enum.java") , getOutputClass()+"Enum", extendClass );
            printEnummethod(ps1);
            ps1.println("}");
        }
        else {
            printEnummethod(ps);
        }
        
        printIDs(ps);
        printEncDecComp(ps);
        
        List<MessageDefinition> messages = new ArrayList<MessageDefinition>();
        for ( MessageDefinition md:messageDefs.values() ) {
            if (!"Object".equals(md.getName()) && use(md) ) {
                messages.add(md);
            }
        }
        Collections.sort(messages, new ClassComparator());
        System.out.println( messages );
        
        printComp(ps,messages);
        printEnc(ps,messages);
        
        if (split) {
            PrintStream ps1 = printClass( new File(outFile.getParentFile(),outFile.getName().substring(0, outFile.getName().lastIndexOf('.'))+"Decode.java") , getOutputClass()+"Decode",    getOutputClass()+"Enum"    );
            printDec(ps1,messages);
            ps1.println("}");
        }
        else {
            printDec(ps,messages);
        }

        ps.println("}");

    }

    private PrintStream printClass(File output,String outClass, String extClass) throws Exception {

PrintStream ps = new PrintStream( output ) {
    private int indent=0;
    @Override
    public void println(String string) {
        int lo = string.lastIndexOf('{');
        int lc = string.lastIndexOf('}');
        int open = string.indexOf('{');
        int close = string.indexOf('}');
        if (close >=0 && (open<0 || close<open)) {
            indent--;
        }
        super.println( "                                ".substring(0, indent*4) + string.trim() );
        if (lo >=0 &&(lc<0 || lo>lc)) {
            indent++;
        }
    }
};
        
ps.println("package "+getOutputPackage()+";");

for (String c:this.objectPackage) {
    ps.println("import "+c+".*;");
}

ps.println("import java.util.Hashtable;");
ps.println("import java.util.Vector;");
if (uses(Set.class)) {
    ps.println("import java.util.Iterator;");
    ps.println("import java.util.Set;");
    ps.println("import java.util.HashSet;");
}
else if (uses(List.class)) {
    ps.println("import java.util.Iterator;");
}
ps.println("import java.io.IOException;");
ps.println("import net.yura.mobile.io."+extendClass+";");
ps.println("import net.yura.mobile.io.proto.CodedOutputStream;");
ps.println("import net.yura.mobile.io.proto.CodedInputStream;");
ps.println("import net.yura.mobile.io.proto.WireFormat;");

ps.println("/**");
ps.println(" * THIS FILE IS GENERATED, DO NOT EDIT");
ps.println(" */");
ps.println("public class "+outClass+" extends "+extClass+" {");

//ps.println("public "+outClass+"() { }"); // empty constructor

    return ps;

    }
    
    public boolean uses(Class c) {
        for ( MessageDefinition md:messageDefs.values() ) {
            for (FieldDefinition field: md.getFields()) {
                if (field.getImplementation() == c) {
                    return true;
                }
            }
        }
        return false;
    }

Hashtable<String,EnumDefinition> enumDefs;
Hashtable<String,MessageDefinition> messageDefs;








    private void printEnummethod(PrintStream ps) {

        Set<String> keys = enumDefs.keySet();

        SortedSet<String> enums = new TreeSet<String>();
        
        String quote = "\"";
        
        if (publicEnums) {
            
            for (String name:keys) {
                EnumDefinition edef = enumDefs.get(name);

                if ( use(edef) ) {
                    Set<Map.Entry<String,Integer>> set = edef.getValues().entrySet();
                    for (Map.Entry<String,Integer> enu:set) {
                        enums.add( enu.getKey() );
                    }
                }
            }
            
            quote="";
            
            for (String name:enums) {
                ps.println("public static final String "+name+" = \""+name+"\";");
            }
            
        }
        
        
        for (String name:keys) {
            EnumDefinition edef = enumDefs.get(name);
            
            if ( use(edef) ) {

                Set<Map.Entry<String,Integer>> set = edef.getValues().entrySet();

ps.println("    public static int get"+name+"Enum(String enu) {");

for (Map.Entry<String,Integer> enu:set) {
    ps.println("    if ("+quote+enu.getKey()+quote+".equals(enu)) return "+enu.getValue()+";");
}

ps.println("        return -1;");
ps.println("    }");
ps.println("    public static String get"+name+"String(int i) {");
ps.println("        switch (i) {");

for (Map.Entry<String,Integer> enu:set) {
    ps.println("        case "+enu.getValue()+": return "+quote+enu.getKey()+quote+";");
}

ps.println("            default: return unknown+i;");
ps.println("        }");
ps.println("    }");

            }

        }
    }





    private void printIDs(PrintStream ps) {

        List<FieldDefinition> set = messageDefs.get("Object").getFields();
                
        for (FieldDefinition enu:set) {
            int num = enu.getID();
            if (num >= 20) {
                MessageDefinition md = getMessageFromEnum( enu.getType() );

                if (use(md)) {
                    ps.println("public static final int "+unCamel(enu.getType())+"="+num+";");
                }
            }
        }
                
                
//ps.println("    @Override");
ps.println("    protected int getObjectTypeEnum(Object obj) {");

ps.println("        if (obj instanceof Hashtable) {");
ps.println("            Hashtable table = (Hashtable)obj;");


for (FieldDefinition enu:set) {
    int num = enu.getID();
    if (num >= 20) {
        MessageDefinition md = getMessageFromEnum( enu.getType() );
        
        if ( use(md) && md.getImplementation() == Hashtable.class ) {

            String line1 ="";
            String line2 ="";
            List<ProtoLoader.FieldDefinition> fields = getMessageFromEnum( enu.getType() ).fields;
            for (ProtoLoader.FieldDefinition field:fields) {
                if (field.required) { //  || field.repeated (we dont need a null vector)
                    line1 = line1 +"\""+field.getName()+"\",";
                }
                else {
                    line2 = line2 +"\""+field.getName()+"\",";
                }
            }
            if (line1.endsWith(",")) line1 = line1.substring(0, line1.length() - 1);
            if (line2.endsWith(",")) line2 = line2.substring(0, line2.length() - 1);

            String req = "".equals(line1)?"EMPTY":"new String[] {"+line1+"}";
            String opt = "".equals(line2)?"EMPTY":"new String[] {"+line2+"}";

            ps.println("    if (hashtableIsMessage(table,"+req+","+opt+")) {");

            ps.println("        return "+ unCamel(enu.getType()) +";");
            ps.println("    }");
        }
    }
}

ps.println("        }");

List<MessageDefinition> messages = new ArrayList<MessageDefinition>();
Hashtable<MessageDefinition,String> messageNames = new Hashtable<MessageDefinition,String>();
for (FieldDefinition enu:set) {
    int num = enu.getID();
    if (num >= 20) {
        String messageName = enu.getType();
        MessageDefinition message = getMessageFromEnum(messageName);
        if( use(message) && message.getImplementation() != Hashtable.class) {
            messages.add(message);
            messageNames.put(message, messageName);
        }
    }
}

Collections.sort(messages,new ClassComparator());

for (MessageDefinition message:messages) {
    ps.println("    if (obj instanceof "+message.getImplementation().getSimpleName()+") {");
    ps.println("        return "+unCamel(messageNames.get(message))+";");
    ps.println("    }");
}

ps.println("        return super.getObjectTypeEnum(obj);");

ps.println("    }");

        
        
    }








    private void printEncDecComp(PrintStream ps) {

        List<FieldDefinition> set = messageDefs.get("Object").getFields();

        
        
        
//ps.println("    @Override");
ps.println("    protected Object decodeObject(CodedInputStream in2,int type) throws IOException {");
ps.println("        switch (type) {");



for (FieldDefinition enu:set) {
    int num = enu.getID();
    if (num >= 20) {
        MessageDefinition md = getMessageFromEnum(enu.getType());
        if (use(md)) {
ps.println("            case "+unCamel(enu.getType())+": return "+decode+getMessageID(md)+"(in2);");
        }
    }
}


ps.println("            default: return super.decodeObject(in2, type);");
ps.println("        }");
ps.println("    }");
//ps.println("    @Override");
ps.println("    protected int computeObjectSize(Object obj,int type) {");
ps.println("        switch (type) {");



for (FieldDefinition enu:set) {
    int num = enu.getID();
    if (num >= 20) {
        MessageDefinition md = getMessageFromEnum(enu.getType());
        if (use(md)) {
ps.println("            case "+unCamel(enu.getType())+": return "+compute+getMessageID(md)+size+"( ("+md.getImplementation().getSimpleName()+")obj );");
        }
    }
}

ps.println("            default: return super.computeObjectSize(obj,type);");
ps.println("        }");
ps.println("    }");
//ps.println("    @Override");
ps.println("    protected void encodeObject(CodedOutputStream out, Object obj,int type) throws IOException {");
ps.println("        switch (type) {");


for (FieldDefinition enu:set) {
    int num = enu.getID();
    if (num >= 20) {
        MessageDefinition md = getMessageFromEnum(enu.getType());
        if (use(md)) {
ps.println("            case "+unCamel(enu.getType())+": "+encode+getMessageID(md)+"( out, ("+md.getImplementation().getSimpleName()+")obj ); break;");
        }
    }
}

ps.println("            default: super.encodeObject(out,obj,type); break;");
ps.println("        }");
ps.println("    }");
        


    }

// #############################################################################
// ############################## compute ######################################
// #############################################################################
    private void printComp(PrintStream ps,List<MessageDefinition> messages) {

        for (MessageDefinition message:messages) {

            ps.println("private int "+compute+getMessageID(message)+size+"("+message.getImplementation().getSimpleName()+" object) {");
            ps.println("    int size=0;");

            printSaveComputeMethod(ps,message,true);

            ps.println("    return size;");
            ps.println("}");

        }
    }

// #############################################################################
// ############################### encode ######################################
// #############################################################################
    private void printEnc(PrintStream ps,List<MessageDefinition> messages) {

        for (MessageDefinition message:messages) {

            ps.println("private void "+encode+getMessageID(message)+"(CodedOutputStream out, "+message.getImplementation().getSimpleName()+" object) throws IOException {");

            printSaveComputeMethod(ps,message,false);

            ps.println("}");

        }
    }

// #############################################################################
// ################################# decode ####################################
// #############################################################################
    private void printDec(PrintStream ps,List<MessageDefinition> messages) {
    
        for (MessageDefinition message:messages) {

            ps.println("protected "+message.getImplementation().getSimpleName()+" "+decode+getMessageID(message)+"(CodedInputStream in2) throws IOException {");
            ps.println("    "+message.getImplementation().getSimpleName()+" object = new "+message.getImplementation().getSimpleName()+"();");

            printLoadMethod(ps,message);

            ps.println("    return object;");
            ps.println("}");

        }

    }



    private void printSaveComputeMethod(PrintStream ps,MessageDefinition message,boolean calc) {

        List<ProtoLoader.FieldDefinition> fields = message.getFields();
        for (ProtoLoader.FieldDefinition field:fields) {

            try {

                final String type;

                if (message.getImplementation() == Hashtable.class) {
                    if (field.getEnumeratedType()!=null) {
                        type = "String";
                    }
                    else if (isPrimitive(field.getType())) {
                        type = primitiveToJavaType(field.getType(),false);
                    }
                    else {
                        MessageDefinition mesDef = messageDefs.get( field.getType() );
                        if (mesDef!=null && mesDef.getImplementation() != null) {
                            type = mesDef.getImplementation().getSimpleName();
                        }
                        else {
                            type = field.getType();
                        }
                    }
                }
                else {

                    if (!field.getRequired() && field.getImplementation()==null) {
                        System.out.println("Skipping field: "+field+" message="+message+" calc="+calc);
                        continue;
                    }

                    if (field.repeated) {
                        if (field.getImplementation().isArray()) {
                            type = field.getImplementation().getComponentType().getSimpleName();
                        }
                        else if (isPrimitive(field.getType())) { // vector
                            type = primitiveToJavaType(field.getType(),false);
                        }
                        else {
                            MessageDefinition mesDef = messageDefs.get( field.getType() );
                            if (mesDef!=null) {
                                type = mesDef.getImplementation().getSimpleName();
                            }
                            else {
                                type = "Hashtable";
                            }
                        }
                    }
                    else {
                        type = field.getImplementation().getSimpleName();
                    }
                }


                if (field.repeated) {
                    if (field.getImplementation() == null || field.getImplementation() == Vector.class || Iterable.class.isAssignableFrom(field.getImplementation()) ) {
                        String forLoop="int c=0;c<"+field.getName()+"Vector.size();c++";
                        String getMethod=field.getName()+"Vector.elementAt(c)";
                        if (field.getImplementation() == null) {
        ps.println("        Vector "+field.getName()+"Vector = (Vector)object.get(\""+field.getName()+"\");");
                        }
                        else if (field.getImplementation() == Vector.class) {
        ps.println("        Vector "+field.getName()+"Vector = object.get"+firstUp(field.getName())+"();");
                        }
                        else {
        ps.println("        Iterable "+field.getName()+"Vector = object.get"+firstUp(field.getName())+"();");
                            forLoop="Iterator it="+field.getName()+"Vector.iterator();it.hasNext();";
                            getMethod="it.next()";
                        }
        ps.println("        if ("+field.getName()+"Vector!=null) {");
        ps.println("            for ("+forLoop+") {");
        ps.println("            "+type+" "+field.getName()+"Value = ("+type+")"+getMethod+";");
                    }
                    else { // must be a array
        ps.println("        "+type+"[] "+field.getName()+"Array = object.get"+firstUp(field.getName())+"();");
        ps.println("        if ("+field.getName()+"Array!=null) {");
        ps.println("            for (int c=0;c<"+field.getName()+"Array.length;c++) {");
        ps.println("            "+type+" "+field.getName()+"Value = "+field.getName()+"Array[c];");
                    }
                                    printSaveCalcField(ps,field,message,calc);
        ps.println("            }");
        ps.println("        }");
                }
                else {

                    if (message.getImplementation() == Hashtable.class) {
                        ps.println(type+" "+field.getName()+"Value = ("+type+")object.get(\""+field.getName()+"\");");
                    }
                    else {
                        ps.println(type+" "+field.getName()+"Value = object.get"+firstUp(field.getName())+"();");
                    }
                    printSaveCalcField(ps,field,message,calc);
                }
            }
            catch (RuntimeException ex) {
                System.err.println("ERROR with "+(calc?"compute":"encode")+" message="+message.getName()+" field="+field.getName()+" type="+field.getType()+" implementation="+message.getImplementation());
                throw ex;
            }
        }
    }

    private void printSaveCalcField(PrintStream ps, FieldDefinition field,MessageDefinition message,boolean calc) {

        boolean optional = !field.required && !field.repeated &&
                (message.getImplementation() == Hashtable.class || !field.getImplementation().isPrimitive());

        if (optional) {
            ps.println("if ("+field.getName()+"Value!=null) {");
        }

        String thing = field.getName()+"Value";
        String type = field.getType();
        if ((message.getImplementation() == Hashtable.class || isWrapperClass( field.getImplementation() )) && !"string".equals(type) && !"bytes".equals(type) && isPrimitive(type) ) {
            thing = thing+"."+getPrimativeFromJavaType( primitiveToJavaType(type,false) ) +"Value()";
        }

        if (field.getEnumeratedType()!=null) {
            thing = "get"+type+"Enum("+thing+")";
            type = "int32";
        }

        String id=null;
        String c = null;
        String e = null;
        String s = null;
        
        String computeString = null;
        if ( !isPrimitive(type) ) {
                if ( "Object".equals(type) ) {
                    computeString = "computeAnonymousObjectSize( "+thing+" )";
                }
                else {

                    MessageDefinition mesDef = messageDefs.get(type);
                    if (mesDef!=null) {
                        id = getMessageID(mesDef);
                        c = compute;
                        e = encode;
                        s = size;
                    }
                    else {
                        id = type;
                        c = "compute";
                        e = "encode";
                        s = "Size";
                    }
                    
                    computeString = c+id+s+"( "+thing+" )";
                }
        }

        String t1=("bytes".equals(type))?"computeByteArraySize("+thing+")":thing;
        if (calc) {
            if ( isPrimitive(type) ) {
                
                t1 = convert(ps,field.getImplementation(),type,t1);

                ps.println("        size = size + CodedOutputStream.compute"+firstUp(type)+"Size("+field.getID()+", "+t1+" );");
            }
            else {
                ps.println("    size = size + CodedOutputStream.computeBytesSize("+field.getID()+", "+computeString+");");
            }
        }
        else {
            if ( isPrimitive(type) ) {

                t1 = convert(ps,field.getImplementation(),type,t1);
                
                ps.println("        out.write"+firstUp(type)+"("+field.getID()+", "+t1+" );");
                if ("bytes".equals(type)) { ps.println("encodeByteArray(out,"+thing+");"); }
            }
            else {
                ps.println("out.writeBytes("+field.getID()+","+computeString+");");
                if ( "Object".equals(type) ) {
                    ps.println("encodeAnonymousObject( out, "+thing+" );");
                }
                else {
                    ps.println(e+id+"( out, "+thing+" );");
                }
            }
        }

        if (optional) {
            ps.println("}");
        }
    }

    public String convert(PrintStream ps, Class clas,String type,String t1){
        if (!isPrimitive(clas)) {
            String javaType = primitiveToJavaType(type, false);
            if ("String".equals(javaType)) {
                ps.println(javaType+" "+t1+"Id = (String)getObjectId("+t1+");");
            }
            else {
                String javaPrimitive = getPrimativeFromJavaType(javaType);
                ps.println(javaPrimitive+" "+t1+"Id = (("+javaType+")getObjectId("+t1+"))."+javaPrimitive+"Value();");
            }
            t1 = t1+"Id";
        }
        return t1;
    }

    boolean isPrimitive(Class clas) {
        return clas==null ||
                clas.isPrimitive() ||
                clas.isArray() ||
                clas==String.class ||
                isWrapperClass(clas) ||
                Iterable.class.isAssignableFrom(clas);
    }

    public static boolean isWrapperClass(Class<?> clazz) {
        try {
            Object object = clazz.getDeclaredField("TYPE").get(null);
            if (object instanceof Class) {
                return ((Class)object).isPrimitive();
            }
        }
        catch (Throwable ex) { }
        return false;
    }




    private void printLoadMethod(PrintStream ps,MessageDefinition message) {

        List<ProtoLoader.FieldDefinition> fields = message.getFields();

        for (ProtoLoader.FieldDefinition field:fields) {
            if (field.getRepeated()) {
                if (field.getImplementation() == Set.class) {
                    ps.println("Set "+field.getName()+"Vector = new HashSet();");
                }
                else {
                    ps.println("Vector "+field.getName()+"Vector = new Vector();");
                }
            }
        }


ps.println("        while (!in2.isAtEnd()) {");
ps.println("            int tag = in2.readTag();");
ps.println("            int fieldNo = WireFormat.getTagFieldNumber(tag);");
//ps.println("            int wireType = WireFormat.getTagWireType(tag);");
    //System.out.println("read field "+fieldNo );
    //System.out.println("wire type "+wireType );


ps.println("            switch(fieldNo) {");

for (ProtoLoader.FieldDefinition field:fields) {

    String type = field.getType();

    if (message.getImplementation() != Hashtable.class && !field.getRequired() && field.getImplementation()==null) {
        System.out.println("Skipping field: "+field+" message="+message+" load");
        continue;
    }


    ps.println("            case "+field.getID()+": {");

    if (field.getEnumeratedType() !=null) {
        ps.println("String value = get"+type+"String( in2.readInt32() );");
    }
    else if (isPrimitive(type)) {
        
        
        if (!isPrimitive(field.getImplementation())) {
            String javaType = primitiveToJavaType(type, true);
            if ("String".equals(javaType)) {
                ps.println(field.getImplementation().getSimpleName()+" value = ("+field.getImplementation().getSimpleName()+")getObjetById( in2.read"+firstUp(type)+"() ,"+field.getImplementation().getSimpleName()+".class);");
            }
            else {
                ps.println(field.getImplementation().getSimpleName()+" value = ("+field.getImplementation().getSimpleName()+")getObjetById( new "+javaType+"(in2.read"+firstUp(type)+"()) ,"+field.getImplementation().getSimpleName()+".class);");
            }
        }
        else if ("string".equals(type) || "bytes".equals(type)) {
            ps.println("        "+primitiveToJavaType(type,true)+" value = in2.read"+firstUp(type)+"();");
        }
        else {
            if (field.getRepeated() || message.getImplementation() == Hashtable.class || isWrapperClass( field.getImplementation() )) {
                if ("bool".equals(type)) {
                    ps.println("    "+primitiveToJavaType(type,true)+" value = in2.read"+firstUp(type)+"()?Boolean.TRUE:Boolean.FALSE;");
                }
                else {
                    ps.println("    "+primitiveToJavaType(type,true)+" value = new "+primitiveToJavaType(type,true)+"(in2.read"+firstUp(type)+"() );");
                }
            }   // only cast things that are not supported by protobuff
            else if (field.getImplementation()==byte.class || field.getImplementation()==char.class || field.getImplementation()==short.class) {
                ps.println("    "+field.getImplementation().getSimpleName()+" value = ("+field.getImplementation().getSimpleName()+")in2.read"+firstUp(type)+"();");
            }
            else {
                ps.println("    "+field.getImplementation().getSimpleName()+" value = in2.read"+firstUp(type)+"();");
            }
        }
    }
    else {
        ps.println("            int size = in2.readBytesSize();");
        ps.println("            int lim = in2.pushLimit(size);");
                        //System.out.println("object size "+size);
        if ("Object".equals(type)) {
            ps.println("        "+type+" value = decodeAnonymousObject(in2);");
        }
        else {

                final String implType;
                final String id;
                final String d;
                MessageDefinition mesDef = messageDefs.get(type);
                if (mesDef!=null) {
                    implType = mesDef.getImplementation().getSimpleName();
                    id = getMessageID(mesDef);
                    d = decode;
                }
                else {
                    implType = type;
                    id = type;
                    d = "decode";
                }


            ps.println("        "+implType+" value = "+d+id+"(in2);");
        }
        ps.println("            in2.popLimit(lim);");
    }

    if (field.getRepeated()) {
        if (field.getImplementation() == Set.class) {
            ps.println("            "+field.getName()+"Vector.add( value );");
        }
        else {
            ps.println("            "+field.getName()+"Vector.addElement( value );");
        }
    }
    else if (message.getImplementation() == Hashtable.class) {
        ps.println("            object.put(\""+field.getName()+"\",value);");
    }
    else {
        ps.println("            object.set"+firstUp(field.getName())+"(value);");
    }

    ps.println("                break;");
    ps.println("            }");
}
ps.println("                default: {");
ps.println("                    in2.skipField(tag);");
ps.println("                    break;");
ps.println("                }");
ps.println("            }");

ps.println("        }");

        for (ProtoLoader.FieldDefinition field:fields) {
            if (field.getRepeated()) {
                if (message.getImplementation() == Hashtable.class) {
                    ps.println("object.put(\""+field.getName()+"\","+field.getName()+"Vector);");
                }
                else if (field.getImplementation().isArray()) {
                    ps.println(""+field.getImplementation().getComponentType().getSimpleName()+"[] "+field.getName()+"Array = new "+field.getImplementation().getComponentType().getSimpleName()+"["+field.getName()+"Vector.size()];");
                    ps.println(""+field.getName()+"Vector.copyInto("+field.getName()+"Array);");
                    ps.println("object.set"+firstUp(field.getName())+"("+field.getName()+"Array);");
                }
                else {
                    ps.println("object.set"+firstUp(field.getName())+"("+field.getName()+"Vector);");
                }
            }
        }

    }









    /**
     * @see MigwClient#unCamel(GeneratedMessage)
     */
    	public static String unCamel(String gm) {
            if (gm==null)
                    return "TYPE_NULL" ;

            String cc = "" ;
            for (char c:gm.toCharArray()) {
                    if (Character.isUpperCase(c)) {
                            cc += "_" ;
                    }
                    cc += Character.toUpperCase(c) ; 
            }
            return "TYPE"+cc ;
	}

        /**
         * this converts the proto notation for field names into java with first letter capitalised
         * @param type hello_world
         * @return HelloWorld
         */
    public static String firstUp(String type) {
        int i;
        while ((i=type.indexOf('_'))>=0) {
            type = type.substring(0, i)+Character.toUpperCase(type.charAt(i+1))+type.substring(i+2);
        }
        return Character.toUpperCase( type.charAt(0) ) + type.substring(1);
    }

    private MessageDefinition getMessageFromEnum(String key) {
        //key = key.substring(5).replaceAll("\\_", ""); // remove the "TYPE_"
        MessageDefinition md = messageDefs.get( key );
        if (md==null) throw new RuntimeException("no message found for type "+key );
        return md;
    }
    private String getMessageID(MessageDefinition md) {
        List<FieldDefinition> edef = messageDefs.get("Object").getFields();
        if (obfuscate) {
            Integer id = null;
            for (FieldDefinition fd:edef) {
                if (fd.getType().equals( md.getName() )) {
                    id = fd.getID();
                    break;
                }
            }
            if (id==null) {
                System.out.println("NO TYPE ENUM FOR "+md);
                return md.getName();
            }
            return id.toString();
        }
        return md.getName();
    }

    private static boolean isPrimitive( String type ) {

        return(
            type.equals( "double"   ) ||
            type.equals( "float"    ) ||
            type.equals( "int32"    ) ||
            type.equals( "int64"    ) ||
            type.equals( "uint32"   ) ||
            type.equals( "uint64"   ) ||
            type.equals( "sint32"   ) ||
            type.equals( "sint64"   ) ||
            type.equals( "fixed32"  ) ||
            type.equals( "fixed64"  ) ||
            type.equals( "sfixed32" ) ||
            type.equals( "sfixed64" ) ||
            type.equals( "bool"     ) ||
            type.equals( "string"   ) ||
            type.equals( "bytes"    )
            );
    }
    private static String primitiveToJavaType( String type,boolean read ) {

        if (
            type.equals( "string"   )
            )
            return "String";

        if (
            type.equals( "int32"    ) ||
            type.equals( "uint32"   ) ||
            type.equals( "sint32"   ) ||
            type.equals( "fixed32"  ) ||
            type.equals( "sfixed32" )
            )
            return "Integer";

        if (
            type.equals( "int64"    ) ||
            type.equals( "uint64"   ) ||
            type.equals( "sint64"   ) ||
            type.equals( "fixed64"  ) ||
            type.equals( "sfixed64" )
            )
            return "Long";

        if (
            type.equals( "bytes"    )
            )
            return read?"byte[]":"Object";

        if (
            type.equals( "double"   )
            )
            return "Double";

        if (
            type.equals( "float"    )
            )
            return "Float";

        if (
            type.equals( "bool"     )
            )
            return "Boolean";

        throw new RuntimeException("bad input "+type);

    }

    private String getPrimativeFromJavaType(String javaType) {

        if (
            javaType.equals( "Integer"    )
            )
            return "int";

        if (
            javaType.equals( "Long"    )
            )
            return "long";

        if (
            javaType.equals( "Double"   )
            )
            return "double";

        if (
            javaType.equals( "Float"    )
            )
            return "float";

        if (
            javaType.equals( "Boolean"     )
            )
            return "boolean";

        throw new RuntimeException("bad input "+javaType);
    }
}
