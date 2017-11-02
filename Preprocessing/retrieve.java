import org.apache.commons.cli.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by qpan on 10/11/2016.
 */
public class retrieve {
    public static void main(String[] args) throws Exception{
        Options options = new Options();

        //create argument options for this program
        Option query = new Option("q", "query", true, "terms for query");
        query.setRequired(true);
        options.addOption(query);

        Option dir = new Option("d", "directory", true, "directory of dict file and post file");
        dir.setRequired(true);
        options.addOption(dir);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        // options are required, if no option is inputted will exist
        try {
            cmd = parser.parse(options, args);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
            return;
        }

        String querytokens = cmd.getOptionValue("query");
        String directory = cmd.getOptionValue("directory");

        // deal with retrieve action
        Query(querytokens, directory);
    }

    public static void Query(String querytokens, String directory) throws IOException, ParseException, TokenMgrError {
        HashMap stopwordlist = new HashMap();

        try{
            String stopfile = "stopwords_en.txt";
            List<String> lines = Files.readAllLines(Paths.get(stopfile), Charset.defaultCharset());
            for(String line: lines){
                stopwordlist.put(line.trim(), line.trim());
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }

        //Initialize the class of assignment 2 to tokenize the key of query
        HashTable table = new HashTable(122062);
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
            findDict(token, table, directory, acc);
        }

        returnResult(acc, directory);
    }

    private static boolean findDict(String token, HashTable table, String directory, HashMap acc) throws IOException{
        boolean result = false;

        //find the index of token
        int index = table.find(token);

        File file = new File(directory, "dict.txt");

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
            if(index < table.getSize()){
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
            findPost(directory, starts, numdoc, acc);
        }
        return  result;
    }

    private static void findPost(String directory, int starts, int numdoc, HashMap acc) throws IOException{
        File file = new File(directory, "post.txt");
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

            if(acc.containsKey(docID)){
                termfrequency += (float)acc.get(docID);
            }
            acc.put(docID, termfrequency);
        }
    }

    private static void returnResult(HashMap acc, String directory) throws IOException{
        //convert a hashtable to a set of key-value pair
        ArrayList lst = new ArrayList(acc.entrySet());

        //sort the set by frequency of token decreasingly
        Collections.sort(lst, new Comparator(){
            public int compare(Object o1, Object o2){
                Map.Entry e1 = (Map.Entry)o1;
                Map.Entry e2 = (Map.Entry)o2;
                float first = (float)e1.getValue() * 100;
                float second = (float)e2.getValue() * 100;

                return ((Float)second).intValue() - ((Float)first).intValue();
            }
        });
        int ind = 0;
        // print the top 10 documents
        while(ind < 10 && lst.size() > ind){
            Map.Entry entry = (Map.Entry)lst.get(ind);
            int docID = Integer.parseInt((String) entry.getKey());
            System.out.println(docID + "\t" + getFileName(directory, docID) + "\t" + formatString(entry.getValue().toString(), 4));
            ind++;
        }
    }

    private static String getFileName(String directory, int docID) throws IOException{
        File file = new File(directory, "mapping.txt");
        FileInputStream fis = new FileInputStream(file.getPath());
        byte[] name = new byte[8];
        fis.skip(9 * docID);
        fis.read(name, 0, 8);
        return new String(name);
    }


    private static String formatString(String value, int length){
        if(value.length() > length){
            return value.substring(0, length);
        }else {
            String format = "%1$-" + length + "s";
            return String.format(format, value);
        }

    }

}
