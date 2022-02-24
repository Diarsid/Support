package diarsid.support.objects.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.stream;

public class Shards implements Iterable<Shards.Frame> {

    public static class Shard<C> {

        public final Class<C> type;

        public Shard(Class<C> type) {
            this.type = type;
        }
    }

    private static class ShardedList<C> {

        final List<C> list;
        final Shard<C> shard;

        public ShardedList(Shard<C> shard) {
            this.shard = shard;
            this.list = new ArrayList<>();
        }
    }

    public static class Frame {

        private final Shards shards;
        private int i;

        public Frame(Shards shards) {
            this.shards = shards;
            this.i = -1;
        }

        public <C> C get(Shard<C> shard) {
            return this.shards.listOf(shard).get(this.i);
        }

        public <C> void set(Shard<C> shard, C v) {
            this.shards.add(this.i, shard, v);
        }
    }

//    private static class MultiListIterator<Index> implements Iterator<Shards.Index> {
//
//        private final Shards shards;
//
//        MultiListIterator(Shards shards) {
//            this.shards = shards;
//        }
//
//        @Override
//        public boolean hasNext() {
//
//        }
//
//        @Override
//        public Shards.Index next() {
//
//        }
//    }

//    @SuppressWarnings("rawtypes")
//    private final Map<Shard, List> listsByShards;

    @SuppressWarnings("rawtypes")
    private final ShardedList[] lists;

    private final int listsQty;

    private final Frame frame;

    private int size;

    @SuppressWarnings("rawtypes")
    public Shards(Shard... shards) {
//        this.listsByShards = new HashMap<>();
//        stream(shards).forEach(shard -> {
//            this.listsByShards.put(shard, new ArrayList<>());
//        });

        this.listsQty = shards.length;
        this.lists = new ShardedList[shards.length];
        for ( int i = 0; i < this.listsQty; i++ ) {
            this.lists[i] = new ShardedList(shards[i]);
        }
        this.frame = new Frame(this);
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    public <C> List<C> listOf(Shard<C> shard) {
        ShardedList shardedList;
        for ( int i = 0; i < this.listsQty; i++ ) {
            shardedList = lists[i];
            if ( shardedList.shard == shard ) {
                return (List<C>) shardedList.list;
            }
        }
        return null;
    }

    private <C> void add(int index, Shard<C> shard, C value) {
        this.size++;
        ShardedList sharded;
        List<C> list;
        for ( int i = 0; i < this.listsQty; i++ ) {
            sharded = lists[i];
            list = sharded.list;
            if ( sharded.shard == shard ) {
                list.set(index, value);
            }
            else {
                if ( list.get(index) == null ) {
                    list.set(index, null);
                }
            }
        }
    }

    private <C> void add(Shard<C> shard, C value) {
        this.size++;
        ShardedList<C> sharded;
        List<C> list;
        for ( int i = 0; i < this.listsQty; i++ ) {
            sharded = lists[i];
            list = sharded.list;
            if ( sharded.shard == shard ) {
                list.add(value);
            }
            else {
                list.add(null);
            }
        }
    }

//    private void to(int i) {
//        ShardedList sharded;
//        List<C> list;
//        for ( int i = 0; i < this.listsQty; i++ ) {
//            sharded = lists[i];
//            list = sharded.list;
//            if ( sharded.shard == shard ) {
//                list.set(i, value);
//            }
//            else {
//                if ( list.get(i) == null ) {
//                    list.set(i, null);
//                }
//            }
//        }
//    }

    public Frame frame(int i) {
        this.frame.i = i;
//        this.to(i);
        return this.frame;
    }

    @Override
    public Iterator<Frame> iterator() {
        return null;
    }

    public static void main(String[] args) {
        Shard<String> one = new Shard<>(String.class);
        Shard<String> two = new Shard<>(String.class);
        Shard<Integer> three = new Shard<>(Integer.class);

        Shards shards = new Shards(one, two, three);

        shards.add(two, "two");

        shards.frame(0).set(one, "string");
        shards.frame(0).set(three, 3);

        String s1 = shards.frame(0).get(one);
        String s2 = shards.frame(0).get(two);
        Integer i = shards.frame(0).get(three);

        int a = 5;
    }
}
