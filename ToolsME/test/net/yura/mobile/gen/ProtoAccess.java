package net.yura.mobile.gen;
import net.yura.tools.mobilegen.model.*;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;
import net.yura.mobile.io.ProtoFileUtil;
import net.yura.mobile.io.proto.CodedOutputStream;
import net.yura.mobile.io.proto.CodedInputStream;
import net.yura.mobile.io.proto.WireFormat;
/**
* THIS FILE IS GENERATED, DO NOT EDIT
*/
public class ProtoAccess extends ProtoFileUtil {
    public static int getTypeEnum(String enu) {
        if ("BOBA".equals(enu)) return 1;
        if ("FRED".equals(enu)) return 2;
        if ("LALA".equals(enu)) return 3;
        return -1;
    }
    public static String getTypeString(int i) {
        switch (i) {
            case 1: return "BOBA";
            case 2: return "FRED";
            case 3: return "LALA";
            default: return unknown+i;
        }
    }
    public static int getOtherThingsTypeEnum(String enu) {
        if ("thing".equals(enu)) return 1;
        if ("stuff".equals(enu)) return 0;
        return -1;
    }
    public static String getOtherThingsTypeString(int i) {
        switch (i) {
            case 1: return "thing";
            case 0: return "stuff";
            default: return unknown+i;
        }
    }
    public static final int TYPE_MESSAGE=20;
    public static final int TYPE_LOGIN=22;
    public static final int TYPE_BOB=10000;
    public static final int TYPE_TEST=10001;
    public static final int TYPE_TEST_OBJECT=10003;
    protected int getObjectTypeEnum(Object obj) {
        if (obj instanceof Hashtable) {
            Hashtable table = (Hashtable)obj;
            if (hashtableIsMessage(table,new String[] {"body"},EMPTY)) {
                return TYPE_MESSAGE;
            }
            if (hashtableIsMessage(table,new String[] {"username","password","intx","intz"},new String[] {"type","tests","image","inty"})) {
                return TYPE_LOGIN;
            }
            if (hashtableIsMessage(table,EMPTY,new String[] {"vec1","vec2","vec3"})) {
                return TYPE_BOB;
            }
        }
        if (obj instanceof TestObject) {
            return TYPE_TEST_OBJECT;
        }
        if (obj instanceof Test) {
            return TYPE_TEST;
        }
        return super.getObjectTypeEnum(obj);
    }
    protected Object decodeObject(CodedInputStream in2,int type) throws IOException {
        switch (type) {
            case TYPE_MESSAGE: return decodeMessage(in2);
            case TYPE_LOGIN: return decodeLogin(in2);
            case TYPE_BOB: return decodeBob(in2);
            case TYPE_TEST: return decodeTest(in2);
            case TYPE_TEST_OBJECT: return decodeTestObject(in2);
            default: return super.decodeObject(in2, type);
        }
    }
    protected int computeObjectSize(Object obj,int type) {
        switch (type) {
            case TYPE_MESSAGE: return computeMessageSize( (Hashtable)obj );
            case TYPE_LOGIN: return computeLoginSize( (Hashtable)obj );
            case TYPE_BOB: return computeBobSize( (Hashtable)obj );
            case TYPE_TEST: return computeTestSize( (Test)obj );
            case TYPE_TEST_OBJECT: return computeTestObjectSize( (TestObject)obj );
            default: return super.computeObjectSize(obj,type);
        }
    }
    protected void encodeObject(CodedOutputStream out, Object obj,int type) throws IOException {
        switch (type) {
            case TYPE_MESSAGE: encodeMessage( out, (Hashtable)obj ); break;
            case TYPE_LOGIN: encodeLogin( out, (Hashtable)obj ); break;
            case TYPE_BOB: encodeBob( out, (Hashtable)obj ); break;
            case TYPE_TEST: encodeTest( out, (Test)obj ); break;
            case TYPE_TEST_OBJECT: encodeTestObject( out, (TestObject)obj ); break;
            default: super.encodeObject(out,obj,type); break;
        }
    }
    private int computeAlbumSize(Hashtable object) {
        int size=0;
        String someStringValue = (String)object.get("someString");
        size = size + CodedOutputStream.computeStringSize(1, someStringValue );
        return size;
    }
    private int computeBobSize(Hashtable object) {
        int size=0;
        Vector vec1Vector = (Vector)object.get("vec1");
        if (vec1Vector!=null) {
            for (int c=0;c<vec1Vector.size();c++) {
                Vector vec1Value = (Vector)vec1Vector.elementAt(c);
                size = size + CodedOutputStream.computeBytesSize(1, computeVectorSize( vec1Value ));
            }
        }
        Vector vec2Vector = (Vector)object.get("vec2");
        if (vec2Vector!=null) {
            for (int c=0;c<vec2Vector.size();c++) {
                Vector vec2Value = (Vector)vec2Vector.elementAt(c);
                size = size + CodedOutputStream.computeBytesSize(2, computeVectorSize( vec2Value ));
            }
        }
        Vector vec3Vector = (Vector)object.get("vec3");
        if (vec3Vector!=null) {
            for (int c=0;c<vec3Vector.size();c++) {
                Vector vec3Value = (Vector)vec3Vector.elementAt(c);
                size = size + CodedOutputStream.computeBytesSize(3, computeVectorSize( vec3Value ));
            }
        }
        return size;
    }
    private int computeClientLoginSuccessSize(Hashtable object) {
        int size=0;
        String sessionIdValue = (String)object.get("sessionId");
        size = size + CodedOutputStream.computeStringSize(1, sessionIdValue );
        Vector albumsVector = (Vector)object.get("albums");
        if (albumsVector!=null) {
            for (int c=0;c<albumsVector.size();c++) {
                Hashtable albumsValue = (Hashtable)albumsVector.elementAt(c);
                size = size + CodedOutputStream.computeBytesSize(3, computeAlbumSize( albumsValue ));
            }
        }
        Hashtable loginValue = (Hashtable)object.get("login");
        size = size + CodedOutputStream.computeBytesSize(6, computeLoginSize( loginValue ));
        Hashtable login2Value = (Hashtable)object.get("login2");
        size = size + CodedOutputStream.computeBytesSize(7, computeLoginSize( login2Value ));
        Integer newPeopleValue = (Integer)object.get("newPeople");
        size = size + CodedOutputStream.computeInt32Size(4, newPeopleValue.intValue() );
        Integer newMessagesValue = (Integer)object.get("newMessages");
        size = size + CodedOutputStream.computeInt32Size(5, newMessagesValue.intValue() );
        return size;
    }
    private int computeLoginSize(Hashtable object) {
        int size=0;
        String usernameValue = (String)object.get("username");
        size = size + CodedOutputStream.computeStringSize(1, usernameValue );
        String passwordValue = (String)object.get("password");
        size = size + CodedOutputStream.computeStringSize(2, passwordValue );
        Vector typeVector = (Vector)object.get("type");
        if (typeVector!=null) {
            for (int c=0;c<typeVector.size();c++) {
                String typeValue = (String)typeVector.elementAt(c);
                size = size + CodedOutputStream.computeInt32Size(3, getTypeEnum(typeValue) );
            }
        }
        Vector testsVector = (Vector)object.get("tests");
        if (testsVector!=null) {
            for (int c=0;c<testsVector.size();c++) {
                TestObject testsValue = (TestObject)testsVector.elementAt(c);
                size = size + CodedOutputStream.computeBytesSize(4, computeTestObjectSize( testsValue ));
            }
        }
        Object imageValue = (Object)object.get("image");
        if (imageValue!=null) {
            size = size + CodedOutputStream.computeBytesSize(5, computeByteArraySize(imageValue) );
        }
        Vector intyVector = (Vector)object.get("inty");
        if (intyVector!=null) {
            for (int c=0;c<intyVector.size();c++) {
                Integer intyValue = (Integer)intyVector.elementAt(c);
                size = size + CodedOutputStream.computeInt32Size(6, intyValue.intValue() );
            }
        }
        Integer intxValue = (Integer)object.get("intx");
        size = size + CodedOutputStream.computeInt32Size(7, intxValue.intValue() );
        Boolean intzValue = (Boolean)object.get("intz");
        size = size + CodedOutputStream.computeBoolSize(8, intzValue.booleanValue() );
        return size;
    }
    private int computeMessageSize(Hashtable object) {
        int size=0;
        Object bodyValue = (Object)object.get("body");
        size = size + CodedOutputStream.computeBytesSize(1, computeAnonymousObjectSize( bodyValue ));
        return size;
    }
    private int computeTestObjectSize(TestObject object) {
        int size=0;
        int idValue = object.getId();
        size = size + CodedOutputStream.computeInt32Size(1204, idValue );
        String nameValue = object.getName();
        if (nameValue!=null) {
            size = size + CodedOutputStream.computeStringSize(1205, nameValue );
        }
        byte ageValue = object.getAge();
        size = size + CodedOutputStream.computeInt32Size(1206, ageValue );
        String myTypeValue = object.getMyType();
        if (myTypeValue!=null) {
            size = size + CodedOutputStream.computeInt32Size(1207, getTypeEnum(myTypeValue) );
        }
        Object bodyValue = object.getBody();
        if (bodyValue!=null) {
            size = size + CodedOutputStream.computeBytesSize(1208, computeAnonymousObjectSize( bodyValue ));
        }
        String[] legsArray = object.getLegs();
        if (legsArray!=null) {
            for (int c=0;c<legsArray.length;c++) {
                String legsValue = legsArray[c];
                size = size + CodedOutputStream.computeStringSize(1209, legsValue );
            }
        }
        byte[] imageValue = object.getImage();
        if (imageValue!=null) {
            size = size + CodedOutputStream.computeBytesSize(1, computeByteArraySize(imageValue) );
        }
        Hashtable organsValue = object.getOrgans();
        if (organsValue!=null) {
            size = size + CodedOutputStream.computeBytesSize(3, computeHashtableSize( organsValue ));
        }
        boolean isAliveValue = object.getIsAlive();
        size = size + CodedOutputStream.computeBoolSize(4, isAliveValue );
        int headsValue = object.getHeads();
        size = size + CodedOutputStream.computeInt32Size(5, headsValue );
        long last_updatedValue = object.getLastUpdated();
        size = size + CodedOutputStream.computeInt64Size(6, last_updatedValue );
        int thingsValue = object.getThings();
        size = size + CodedOutputStream.computeInt32Size(7, thingsValue );
        Test and_one_insideValue = object.getAndOneInside();
        if (and_one_insideValue!=null) {
            size = size + CodedOutputStream.computeBytesSize(8, computeTestSize( and_one_insideValue ));
        }
        Vector numbersVector = object.getNumbers();
        if (numbersVector!=null) {
            for (int c=0;c<numbersVector.size();c++) {
                Integer numbersValue = (Integer)numbersVector.elementAt(c);
                size = size + CodedOutputStream.computeInt32Size(9, numbersValue );
            }
        }
        Object[] objectsArray = object.getObjects();
        if (objectsArray!=null) {
            for (int c=0;c<objectsArray.length;c++) {
                Object objectsValue = objectsArray[c];
                size = size + CodedOutputStream.computeBytesSize(20, computeAnonymousObjectSize( objectsValue ));
            }
        }
        Test test_by_idValue = object.getTestById();
        if (test_by_idValue!=null) {
            int test_by_idValueId = ((Integer)getObjectId(test_by_idValue)).intValue();
            size = size + CodedOutputStream.computeInt32Size(30, test_by_idValueId );
        }
        Hashtable login_by_idValue = object.getLoginById();
        if (login_by_idValue!=null) {
            int login_by_idValueId = ((Integer)getObjectId(login_by_idValue)).intValue();
            size = size + CodedOutputStream.computeInt32Size(31, login_by_idValueId );
        }
        Object object_by_idValue = object.getObjectById();
        if (object_by_idValue!=null) {
            String object_by_idValueId = (String)getObjectId(object_by_idValue);
            size = size + CodedOutputStream.computeStringSize(32, object_by_idValueId );
        }
        return size;
    }
    private int computeTestSize(Test object) {
        int size=0;
        int idValue = object.getId();
        size = size + CodedOutputStream.computeInt32Size(1201, idValue );
        return size;
    }
    private void encodeAlbum(CodedOutputStream out, Hashtable object) throws IOException {
        String someStringValue = (String)object.get("someString");
        out.writeString(1, someStringValue );
    }
    private void encodeBob(CodedOutputStream out, Hashtable object) throws IOException {
        Vector vec1Vector = (Vector)object.get("vec1");
        if (vec1Vector!=null) {
            for (int c=0;c<vec1Vector.size();c++) {
                Vector vec1Value = (Vector)vec1Vector.elementAt(c);
                out.writeBytes(1,computeVectorSize( vec1Value ));
                encodeVector( out, vec1Value );
            }
        }
        Vector vec2Vector = (Vector)object.get("vec2");
        if (vec2Vector!=null) {
            for (int c=0;c<vec2Vector.size();c++) {
                Vector vec2Value = (Vector)vec2Vector.elementAt(c);
                out.writeBytes(2,computeVectorSize( vec2Value ));
                encodeVector( out, vec2Value );
            }
        }
        Vector vec3Vector = (Vector)object.get("vec3");
        if (vec3Vector!=null) {
            for (int c=0;c<vec3Vector.size();c++) {
                Vector vec3Value = (Vector)vec3Vector.elementAt(c);
                out.writeBytes(3,computeVectorSize( vec3Value ));
                encodeVector( out, vec3Value );
            }
        }
    }
    private void encodeClientLoginSuccess(CodedOutputStream out, Hashtable object) throws IOException {
        String sessionIdValue = (String)object.get("sessionId");
        out.writeString(1, sessionIdValue );
        Vector albumsVector = (Vector)object.get("albums");
        if (albumsVector!=null) {
            for (int c=0;c<albumsVector.size();c++) {
                Hashtable albumsValue = (Hashtable)albumsVector.elementAt(c);
                out.writeBytes(3,computeAlbumSize( albumsValue ));
                encodeAlbum( out, albumsValue );
            }
        }
        Hashtable loginValue = (Hashtable)object.get("login");
        out.writeBytes(6,computeLoginSize( loginValue ));
        encodeLogin( out, loginValue );
        Hashtable login2Value = (Hashtable)object.get("login2");
        out.writeBytes(7,computeLoginSize( login2Value ));
        encodeLogin( out, login2Value );
        Integer newPeopleValue = (Integer)object.get("newPeople");
        out.writeInt32(4, newPeopleValue.intValue() );
        Integer newMessagesValue = (Integer)object.get("newMessages");
        out.writeInt32(5, newMessagesValue.intValue() );
    }
    private void encodeLogin(CodedOutputStream out, Hashtable object) throws IOException {
        String usernameValue = (String)object.get("username");
        out.writeString(1, usernameValue );
        String passwordValue = (String)object.get("password");
        out.writeString(2, passwordValue );
        Vector typeVector = (Vector)object.get("type");
        if (typeVector!=null) {
            for (int c=0;c<typeVector.size();c++) {
                String typeValue = (String)typeVector.elementAt(c);
                out.writeInt32(3, getTypeEnum(typeValue) );
            }
        }
        Vector testsVector = (Vector)object.get("tests");
        if (testsVector!=null) {
            for (int c=0;c<testsVector.size();c++) {
                TestObject testsValue = (TestObject)testsVector.elementAt(c);
                out.writeBytes(4,computeTestObjectSize( testsValue ));
                encodeTestObject( out, testsValue );
            }
        }
        Object imageValue = (Object)object.get("image");
        if (imageValue!=null) {
            out.writeBytes(5, computeByteArraySize(imageValue) );
            encodeByteArray(out,imageValue);
        }
        Vector intyVector = (Vector)object.get("inty");
        if (intyVector!=null) {
            for (int c=0;c<intyVector.size();c++) {
                Integer intyValue = (Integer)intyVector.elementAt(c);
                out.writeInt32(6, intyValue.intValue() );
            }
        }
        Integer intxValue = (Integer)object.get("intx");
        out.writeInt32(7, intxValue.intValue() );
        Boolean intzValue = (Boolean)object.get("intz");
        out.writeBool(8, intzValue.booleanValue() );
    }
    private void encodeMessage(CodedOutputStream out, Hashtable object) throws IOException {
        Object bodyValue = (Object)object.get("body");
        out.writeBytes(1,computeAnonymousObjectSize( bodyValue ));
        encodeAnonymousObject( out, bodyValue );
    }
    private void encodeTestObject(CodedOutputStream out, TestObject object) throws IOException {
        int idValue = object.getId();
        out.writeInt32(1204, idValue );
        String nameValue = object.getName();
        if (nameValue!=null) {
            out.writeString(1205, nameValue );
        }
        byte ageValue = object.getAge();
        out.writeInt32(1206, ageValue );
        String myTypeValue = object.getMyType();
        if (myTypeValue!=null) {
            out.writeInt32(1207, getTypeEnum(myTypeValue) );
        }
        Object bodyValue = object.getBody();
        if (bodyValue!=null) {
            out.writeBytes(1208,computeAnonymousObjectSize( bodyValue ));
            encodeAnonymousObject( out, bodyValue );
        }
        String[] legsArray = object.getLegs();
        if (legsArray!=null) {
            for (int c=0;c<legsArray.length;c++) {
                String legsValue = legsArray[c];
                out.writeString(1209, legsValue );
            }
        }
        byte[] imageValue = object.getImage();
        if (imageValue!=null) {
            out.writeBytes(1, computeByteArraySize(imageValue) );
            encodeByteArray(out,imageValue);
        }
        Hashtable organsValue = object.getOrgans();
        if (organsValue!=null) {
            out.writeBytes(3,computeHashtableSize( organsValue ));
            encodeHashtable( out, organsValue );
        }
        boolean isAliveValue = object.getIsAlive();
        out.writeBool(4, isAliveValue );
        int headsValue = object.getHeads();
        out.writeInt32(5, headsValue );
        long last_updatedValue = object.getLastUpdated();
        out.writeInt64(6, last_updatedValue );
        int thingsValue = object.getThings();
        out.writeInt32(7, thingsValue );
        Test and_one_insideValue = object.getAndOneInside();
        if (and_one_insideValue!=null) {
            out.writeBytes(8,computeTestSize( and_one_insideValue ));
            encodeTest( out, and_one_insideValue );
        }
        Vector numbersVector = object.getNumbers();
        if (numbersVector!=null) {
            for (int c=0;c<numbersVector.size();c++) {
                Integer numbersValue = (Integer)numbersVector.elementAt(c);
                out.writeInt32(9, numbersValue );
            }
        }
        Object[] objectsArray = object.getObjects();
        if (objectsArray!=null) {
            for (int c=0;c<objectsArray.length;c++) {
                Object objectsValue = objectsArray[c];
                out.writeBytes(20,computeAnonymousObjectSize( objectsValue ));
                encodeAnonymousObject( out, objectsValue );
            }
        }
        Test test_by_idValue = object.getTestById();
        if (test_by_idValue!=null) {
            int test_by_idValueId = ((Integer)getObjectId(test_by_idValue)).intValue();
            out.writeInt32(30, test_by_idValueId );
        }
        Hashtable login_by_idValue = object.getLoginById();
        if (login_by_idValue!=null) {
            int login_by_idValueId = ((Integer)getObjectId(login_by_idValue)).intValue();
            out.writeInt32(31, login_by_idValueId );
        }
        Object object_by_idValue = object.getObjectById();
        if (object_by_idValue!=null) {
            String object_by_idValueId = (String)getObjectId(object_by_idValue);
            out.writeString(32, object_by_idValueId );
        }
    }
    private void encodeTest(CodedOutputStream out, Test object) throws IOException {
        int idValue = object.getId();
        out.writeInt32(1201, idValue );
    }
    protected Hashtable decodeAlbum(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1: {
                    String value = in2.readString();
                    object.put("someString",value);
                    break;
                }
                default: {
                    in2.skipField(tag);
                    break;
                }
            }
        }
        return object;
    }
    protected Hashtable decodeBob(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        Vector vec1Vector = new Vector();
        Vector vec2Vector = new Vector();
        Vector vec3Vector = new Vector();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Vector value = decodeVector(in2);
                    in2.popLimit(lim);
                    vec1Vector.addElement( value );
                    break;
                }
                case 2: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Vector value = decodeVector(in2);
                    in2.popLimit(lim);
                    vec2Vector.addElement( value );
                    break;
                }
                case 3: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Vector value = decodeVector(in2);
                    in2.popLimit(lim);
                    vec3Vector.addElement( value );
                    break;
                }
                default: {
                    in2.skipField(tag);
                    break;
                }
            }
        }
        object.put("vec1",vec1Vector);
        object.put("vec2",vec2Vector);
        object.put("vec3",vec3Vector);
        return object;
    }
    protected Hashtable decodeClientLoginSuccess(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        Vector albumsVector = new Vector();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1: {
                    String value = in2.readString();
                    object.put("sessionId",value);
                    break;
                }
                case 3: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Hashtable value = decodeAlbum(in2);
                    in2.popLimit(lim);
                    albumsVector.addElement( value );
                    break;
                }
                case 6: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Hashtable value = decodeLogin(in2);
                    in2.popLimit(lim);
                    object.put("login",value);
                    break;
                }
                case 7: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Hashtable value = decodeLogin(in2);
                    in2.popLimit(lim);
                    object.put("login2",value);
                    break;
                }
                case 4: {
                    Integer value = new Integer(in2.readInt32() );
                    object.put("newPeople",value);
                    break;
                }
                case 5: {
                    Integer value = new Integer(in2.readInt32() );
                    object.put("newMessages",value);
                    break;
                }
                default: {
                    in2.skipField(tag);
                    break;
                }
            }
        }
        object.put("albums",albumsVector);
        return object;
    }
    protected Hashtable decodeLogin(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        Vector typeVector = new Vector();
        Vector testsVector = new Vector();
        Vector intyVector = new Vector();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1: {
                    String value = in2.readString();
                    object.put("username",value);
                    break;
                }
                case 2: {
                    String value = in2.readString();
                    object.put("password",value);
                    break;
                }
                case 3: {
                    String value = getTypeString( in2.readInt32() );
                    typeVector.addElement( value );
                    break;
                }
                case 4: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    TestObject value = decodeTestObject(in2);
                    in2.popLimit(lim);
                    testsVector.addElement( value );
                    break;
                }
                case 5: {
                    byte[] value = in2.readBytes();
                    object.put("image",value);
                    break;
                }
                case 6: {
                    Integer value = new Integer(in2.readInt32() );
                    intyVector.addElement( value );
                    break;
                }
                case 7: {
                    Integer value = new Integer(in2.readInt32() );
                    object.put("intx",value);
                    break;
                }
                case 8: {
                    Boolean value = in2.readBool()?Boolean.TRUE:Boolean.FALSE;
                    object.put("intz",value);
                    break;
                }
                default: {
                    in2.skipField(tag);
                    break;
                }
            }
        }
        object.put("type",typeVector);
        object.put("tests",testsVector);
        object.put("inty",intyVector);
        return object;
    }
    protected Hashtable decodeMessage(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Object value = decodeAnonymousObject(in2);
                    in2.popLimit(lim);
                    object.put("body",value);
                    break;
                }
                default: {
                    in2.skipField(tag);
                    break;
                }
            }
        }
        return object;
    }
    protected TestObject decodeTestObject(CodedInputStream in2) throws IOException {
        TestObject object = new TestObject();
        Vector legsVector = new Vector();
        Vector numbersVector = new Vector();
        Vector objectsVector = new Vector();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1204: {
                    int value = in2.readInt32();
                    object.setId(value);
                    break;
                }
                case 1205: {
                    String value = in2.readString();
                    object.setName(value);
                    break;
                }
                case 1206: {
                    byte value = (byte)in2.readInt32();
                    object.setAge(value);
                    break;
                }
                case 1207: {
                    String value = getTypeString( in2.readInt32() );
                    object.setMyType(value);
                    break;
                }
                case 1208: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Object value = decodeAnonymousObject(in2);
                    in2.popLimit(lim);
                    object.setBody(value);
                    break;
                }
                case 1209: {
                    String value = in2.readString();
                    legsVector.addElement( value );
                    break;
                }
                case 1: {
                    byte[] value = in2.readBytes();
                    object.setImage(value);
                    break;
                }
                case 3: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Hashtable value = decodeHashtable(in2);
                    in2.popLimit(lim);
                    object.setOrgans(value);
                    break;
                }
                case 4: {
                    boolean value = in2.readBool();
                    object.setIsAlive(value);
                    break;
                }
                case 5: {
                    int value = in2.readInt32();
                    object.setHeads(value);
                    break;
                }
                case 6: {
                    long value = in2.readInt64();
                    object.setLastUpdated(value);
                    break;
                }
                case 7: {
                    int value = in2.readInt32();
                    object.setThings(value);
                    break;
                }
                case 8: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Test value = decodeTest(in2);
                    in2.popLimit(lim);
                    object.setAndOneInside(value);
                    break;
                }
                case 9: {
                    Integer value = new Integer(in2.readInt32() );
                    numbersVector.addElement( value );
                    break;
                }
                case 20: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Object value = decodeAnonymousObject(in2);
                    in2.popLimit(lim);
                    objectsVector.addElement( value );
                    break;
                }
                case 30: {
                    Test value = (Test)getObjetById( new Integer(in2.readInt32()) ,Test.class);
                    object.setTestById(value);
                    break;
                }
                case 31: {
                    Hashtable value = (Hashtable)getObjetById( new Integer(in2.readInt32()) ,Hashtable.class);
                    object.setLoginById(value);
                    break;
                }
                case 32: {
                    Object value = (Object)getObjetById( in2.readString() ,Object.class);
                    object.setObjectById(value);
                    break;
                }
                default: {
                    in2.skipField(tag);
                    break;
                }
            }
        }
        String[] legsArray = new String[legsVector.size()];
        legsVector.copyInto(legsArray);
        object.setLegs(legsArray);
        object.setNumbers(numbersVector);
        Object[] objectsArray = new Object[objectsVector.size()];
        objectsVector.copyInto(objectsArray);
        object.setObjects(objectsArray);
        return object;
    }
    protected Test decodeTest(CodedInputStream in2) throws IOException {
        Test object = new Test();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1201: {
                    int value = in2.readInt32();
                    object.setId(value);
                    break;
                }
                default: {
                    in2.skipField(tag);
                    break;
                }
            }
        }
        return object;
    }
}
