import com.src.searchengine.ResultElement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by qpan on 11/7/2016.
 */
public class Search extends HttpServlet{
    private int Table_Size = 122062 * 3;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String query = request.getParameter("q");
        ArrayList<ResultElement> results = new ArrayList<>();

        try {
            long starttime = System.currentTimeMillis();
            int result =  retrieve(query, results);
            long totalTime = System.currentTimeMillis() - starttime;

            request.setAttribute("Size", result);
            request.setAttribute("Time", (float)totalTime/1000);
            request.setAttribute("Results", results);
            request.setAttribute("query", query);
        }catch (ParseException ex){
            System.out.println("Parsing Error! ");
        }
        this.getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private int retrieve(String querytokens, ArrayList<ResultElement> results) throws  IOException, ParseException{
        HashMap stopwordlist = new HashMap();
        GetStopWordList(stopwordlist);

        InputStream stream = new ByteArrayInputStream(querytokens.getBytes(StandardCharsets.UTF_8));
        index parser = new index(stream);

        HashMap map = new HashMap();

        //tokenize the key of query, tokens are saved to HashMap
        parser.Start(map, stopwordlist, System.out);

        HashMap acc = new HashMap();
        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry pair = (Map.Entry)iterator.next();
            String token = (String) pair.getKey();
            //int weight = (int) pair.getValue();
            findDict(token, acc);
        }
       return returnResult(acc, results);
    }

    private int returnResult(HashMap acc, ArrayList<ResultElement> results) throws IOException {
        //convert a hashtable to a set of key-value pair
        ArrayList lst = new ArrayList(acc.entrySet());

        //sort the set by frequency of token decreasingly
        Collections.sort(lst, new Comparator() {
            public int compare(Object o1, Object o2) {
                Map.Entry e1 = (Map.Entry) o1;
                Map.Entry e2 = (Map.Entry) o2;
                float first = ((ResultElement) e1.getValue()).TermFrequency * 100;
                float second = ((ResultElement) e2.getValue()).TermFrequency * 100;

                return ((Float) second).intValue() - ((Float) first).intValue();
            }
        });
        int ind = 0;
        // print the top 10 documents
        while (ind < 10 && lst.size() > ind) {
            Map.Entry entry = (Map.Entry) lst.get(ind);
            ResultElement element = (ResultElement) entry.getValue();
            results.add(element);
//            int docID = Integer.parseInt((String) entry.getKey());
//            System.out.println(docID + "\t" + getFileName(directory, docID) + "\t" + formatString(entry.getValue().toString(), 4));
            ind++;
        }
        return lst.size();
    }

    private void GetStopWordList(HashMap stopwordlist) throws  IOException{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("stopwords_en.txt").getFile());

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = null;
        while((line = reader.readLine()) != null){
            stopwordlist.put(line.trim(), line.trim());
        }
    }

    private boolean findDict(String token,  HashMap acc) throws IOException{
        boolean result = false;

        //find the index of token
        int index = find(token);

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dict.txt").getFile());

        FileInputStream fis = new FileInputStream(file);

        // skip the first index * 26 bytes and read 10 bytes from dict file
        byte[] key = new byte[10];
        fis.skip(index * 26);
        fis.read(key, 0, 10);
        String value = new String(key);

        String formatToken = formatString(token, 10);
        String empty = formatString("", 10);

        // if the string read from the dict file is not empty
        // and doesn't match the key of query
        // skip the next 16 bytes, and read 10 bytes
        // until the string is match the query key or the string is empty
        while(!value.equals(formatToken) && !value.equals(empty)){
            index++;

            // if the index is larger than the size of hashtable, go back to index 0
            if(index < Table_Size){
                fis.skip(16);
                fis.read(key, 0, 10);
                value = new String(key);
            }else {
                index = 0;
                fis = new FileInputStream(file);
                fis.read(key, 0, 10);
                value = new String(key);
            }
        }

        // if the final value match the query key, then index found
        if(value.equals(formatToken)){
            result = true;
        }

        if(result){
            // read the number of document
            byte[] b_numdoc = new byte[6];
            fis.read(b_numdoc, 0, 6);
            int numdoc = Integer.parseInt(new String(b_numdoc).trim());

            // read the starts in post file
            byte[] b_starts = new byte[10];
            fis.read(b_starts, 0, 10);
            int starts = Integer.parseInt(new String(b_starts).trim());

            //process post file
            findPost(token, starts, numdoc, acc);
        }
        return  result;
    }

    public int find(String str)
    {
//        int size = Table_Size;
        long sum=0;
        long index;

        //add all the characters of the string together
        for(int i=0;i<str.length();i++)
            sum=(sum*19)+str.charAt(i); //multiply sum by 19 and add byte value of char

        if(sum < 0)				// If calculation of sum was negative, make it positive
            sum = sum * -1;

        index= sum%Table_Size;
        int index2 = (int) index;

        return index2;
    }

    private void findPost(String token, int starts, int numdoc, HashMap acc) throws IOException{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("post.txt").getFile());

        FileInputStream fis = new FileInputStream(file.getPath());
        byte[] b_docID = new byte[6];
        byte[] b_termfrequency = new byte[5];

        // skips the first starts * 11 bytes from post file
        fis.skip(11 * starts);
        for(int i = 0; i < numdoc; i++){
            fis.read(b_docID, 0, 6);
            fis.read(b_termfrequency, 0, 5);

            String docID = new String(b_docID).trim();
            float termfrequency = Float.parseFloat(new String(b_termfrequency).trim());

            ResultElement element;
            if(acc.containsKey(docID)){
                element = (ResultElement) acc.get(docID);
                element.TermFrequency += termfrequency;
                element.Matches += " , " + token;
            }else {
                String name = getFileName(Integer.parseInt(docID));
                element = new ResultElement(name);
                element.TermFrequency = termfrequency;
                element.Matches = token;
                acc.put(docID, element);
            }

        }
    }

    private String getFileName(int docID) throws IOException{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("mapping.txt").getFile());

        FileInputStream fis = new FileInputStream(file.getPath());
        byte[] name = new byte[8];
        fis.skip(9 * docID);
        fis.read(name, 0, 8);
        return new String(name);
    }

    private String formatString(String value, int length){
        if(value.length() > length){
            return value.substring(0, length);
        }else {
            String format = "%1$-" + length + "s";
            return String.format(format, value);
        }

    }


}
