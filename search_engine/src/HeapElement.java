/**
 * Created by qpan on 10/25/2016.
 */
public class HeapElement {
    public String _docID;
    public double _termweight;

    public HeapElement(String docid, float tw){
        _docID = docid;
        _termweight = tw;
    }
}
