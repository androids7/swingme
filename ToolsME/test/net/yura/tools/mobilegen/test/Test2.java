package net.yura.tools.mobilegen.test;

import com.google.protobuf.GeneratedMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.yura.mobile.gen.ProtoAccess;
import net.yura.server.gen.TestProtos;
import net.yura.server.gen.TestProtos.Bob;
import net.yura.server.gen.TestProtos.Test;
import net.yura.server.gen.TestProtos.TestObject;
import net.yura.server.gen.TestProtos.Vector;

/**
 * @author Yura Mamyrin
 */
public class Test2 {

    public static void main(String... args) throws Exception {


        Test test1 = Test.newBuilder().setId(55).build();
        TestObject.Builder test2 = TestObject.newBuilder().setId(5335).setAge(-5).setName("Gina");
        test2.setHeads(5).setLastUpdated(0).setThings(TestProtos.OtherThingsType.stuff).setIsAlive(false);
        Test test3 = Test.newBuilder().setId(53344335).build();

        Vector.Builder addressBook = Vector.newBuilder();

        addressBook.addElements( getObject(test1) );
        addressBook.addElements( getObject(test2.build() ) );
        addressBook.addElements( getObject(test3) );

        addressBook.addElements( wrapString("bob the builder") );


        Vector v = addressBook.build();
//System.out.println("v=" + v);
//        v.writeTo(out);

        Bob.Builder bob = Bob.newBuilder();

        List<Vector> vec1 = new ArrayList<Vector>();
        vec1.add(v);
        vec1.add(v);
        vec1.add(v);

        bob.addAllVec1(vec1);
        bob.addAllVec2(vec1);
        bob.addAllVec3(vec1);
        
        Bob b = bob.build();

        byte[] bbytes = getObject(b).toByteArray();

        com.google.protobuf.GeneratedMessage obj = getObject( net.yura.server.gen.TestProtos.Object.parseFrom( new ByteArrayInputStream(bbytes) ) );

System.out.println("equals="+b.equals(obj)+" getting string: "+ ((Bob)obj).getVec1(0).getElements(3).getString() );


        ProtoAccess access = new ProtoAccess();

        Object out = access.load( new ByteArrayInputStream(bbytes) , bbytes.length );

        System.out.println("out="+out);

        // TODO check against sample object

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        access.save(bout, out);

        com.google.protobuf.GeneratedMessage obj2 = getObject( net.yura.server.gen.TestProtos.Object.parseFrom( new ByteArrayInputStream(bout.toByteArray()) ) );
        
System.out.println("equals="+b.equals(obj2)+" getting string: "+ ((Bob)obj2).getVec1(0).getElements(3).getString() );



    }

    public static net.yura.server.gen.TestProtos.Object wrapString(String s) throws Exception {
        net.yura.server.gen.TestProtos.Object.Builder sb = net.yura.server.gen.TestProtos.Object.newBuilder();
        sb.setString(s);
        return sb.build();
    }

    public static net.yura.server.gen.TestProtos.Object getObject(com.google.protobuf.GeneratedMessage message) throws Exception {
        net.yura.server.gen.TestProtos.Object.Builder mbb = net.yura.server.gen.TestProtos.Object.newBuilder();
        mbb.getClass().getMethod("set"+message.getClass().getSimpleName(),message.getClass()).invoke(mbb, message) ;
        return mbb.build();
    }

    public static GeneratedMessage getObject(net.yura.server.gen.TestProtos.Object object) throws Exception {
        Collection list = object.getAllFields().values();
        if (list.isEmpty()) return null;
        return (GeneratedMessage)list.iterator().next();
    }

}
