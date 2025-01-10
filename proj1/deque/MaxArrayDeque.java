package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }
    public T max(){
        return max(comparator);
    }
    private T max(Comparator<T> c){
        if(isEmpty()){
            return null;
        }
        T maxItem=get(0);
        for(int i=1;i<size();i++){
            if(c.compare(maxItem,get(i))<0){
                maxItem=get(i);
            }
        }
        return maxItem;
    }
}
