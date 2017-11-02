import java.util.ArrayList;

/**
 * Created by qpan on 10/25/2016.
 */
public class Heap {
    private ArrayList<HeapElement> heapList;

    public Heap(){
        heapList = new ArrayList<>();
    }

    public void Insert(String docid, float tw){
        int index = Find(docid, tw);
        BuildMaxHeap(index);
    }

    public int Size(){return  heapList.size();}

    public HeapElement Maximum(){
        if(heapList.size() > 0) {
            HeapElement element = heapList.get(0);
            int last = heapList.size() - 1;
            heapList.set(0, heapList.get(last));
            heapList.remove(last);
            MaxHeapify(0);
            return element;
        }
        return null;
    }


    private int Find(String docid, float tw){
        HeapElement element;
        for(int i = 0; i< heapList.size(); i++){
            element = heapList.get(i);
            if(element._docID.equals(docid)){
                element._termweight += tw;
                return i;
            }
        }
        element = new HeapElement(docid, tw);
        heapList.add(element);
        return heapList.size() - 1;
    }

    private void BuildMaxHeap(int index){
        if(index > 0) {
            int parent = (index - 1) / 2;
            HeapElement current = heapList.get(index);
            HeapElement parentElement = heapList.get(parent);
            if(current._termweight > parentElement._termweight){
                heapList.set(index, parentElement);
                heapList.set(parent, current);
                BuildMaxHeap(parent);
            }
        }
    }


    private void MaxHeapify(int index){
        int left = index * 2 + 1;
        int right = index * 2 + 2;

        int length = heapList.size();
        int largest = index;

        if(left < length && heapList.get(left)._termweight > heapList.get(index)._termweight){
            largest = left;
        }
        if(right < length && heapList.get(right)._termweight > heapList.get(largest)._termweight){
            largest = right;
        }

        if(largest != index){
            HeapElement tmp = heapList.get(index);
            heapList.set(index, heapList.get(largest));
            heapList.set(largest, tmp);
            MaxHeapify(largest);
        }
    }
}

