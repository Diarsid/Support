package diarsid.support.strings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import diarsid.support.objects.reflection.B;
import diarsid.support.objects.reflection.C;

public class MultilineMessageDemo {

    public static void main(String[] args) {
        MultilineMessage message = new MultilineMessage("[MESSAGE]");

        List<Object> objs = List.of(new Object(), new Object(), new Object());

        message.newLine().add("objects:");
        message.startTable(1, "hash", "toString", "class");
        for ( Object object : objs ) {
            message.table()
                    .line().nextCell().add(object.hashCode())
                    .line().nextCell().add(object.toString())
                    .line().nextCell().add(object.getClass().getSimpleName())
                    .line().ends();
        }
        message.table().ends();

        message.startTable("hash", "toString", "class").addAsLines(
                objs,
                (object, line) -> line
                        .cell().add(object.hashCode())
                        .next().add(object.toString())
                        .next().add(object.getClass().getSimpleName()));

        message.startVerticalMap(1)
                .key(1).value("aaa")
                .key("xxx").value(1)
                .key("fdsfdfdfs").value(true)
                .ends();

        message.newLine().add("next map:");

        String n = null;

        message.startVerticalMap(2)
                .value(n)
                .key(n)
                .key(n).value(n)
                .key(n).value("aaa")
                .key("xxxxxxxx").value(n)
                .key(n).value(n)
                .ends();

        System.out.println(message.compose());

        MultilineMessage messageAutoTable = new MultilineMessage("[AUTO TABLE]");

        List<C> cs = new ArrayList<>();

        C c;
        for ( int i = 0; i < 5; i++ ) {
            c = new C();
            c.i = i;
            c.is = true;
            c.list = List.of(i+1, i+2);
            ((B) c).list = List.of("aaa", "bbb");
            c.name = "name_" + i;
            c.number = new AtomicReference<>(new BigDecimal("" + i + ".2"));
            cs.add(c);
        }

        messageAutoTable.addAsTable(cs);

        messageAutoTable.newLine().add("as cell values:");
        messageAutoTable
                .startTable("i", "is", "number")
                .addAsCellValues(
                        cs,
                        cc -> List.of(cc.i, cc.is, cc.number))
                .ends();

        System.out.println(messageAutoTable.compose());
    }
}
