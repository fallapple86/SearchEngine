/* Generated By:JavaCC: Do not edit this line. index.java */
    import java.io.PrintStream;
    import java.io.File;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.util.*;


    public class index implements indexConstants {
        public static void main(String[] args) throws ParseException, TokenMgrError{
            //make sure the number of input arguments should be 2(one input directory and one output directory)
            if(args.length != 2){
                System.out.println("The number of parameters should be 2. But now has " + args.length);
                for(String p: args){
                    System.out.println(p);
                }
                return;

            }
            //process files
            Process(args[0], args[1]);
        }

        private static void Process(String inputdir, String outputdir) throws ParseException, TokenMgrError{
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

            //the input folder
            File folder = new File(inputdir);
            //the output folder
            File output = new File(outputdir);

            //if the input folder doesn't exist, report an error
            if(!folder.isDirectory()){
                System.out.println("This path does not exist or it is not a directory. " + inputdir);
                return;
            }

            //if the output folder doesn't exist, create the folder
            if(!output.isDirectory()){
                output.mkdirs();
            }

            //read all the files within the input folder
            File[] listOfFiles = folder.listFiles();

            //initial one hashtable to save all tokens and their frequency
            HashTable table = new HashTable(122062);

            //local hashtable
            HashMap map = new HashMap();

            //mapping file
            ArrayList<String> mapping = new ArrayList<String>();

            //deal with all the files under the input folder
            for(File file: listOfFiles){
                if(file.isFile()){
                    //get the filename of the current file
                    String inputfile = file.getName();
                    //get the path of the current file
                    String path = file.getPath();

                    try {
                        // initialize the tokenizer
                        index parser = new index(new java.io.FileInputStream(path));

                        //initial an array to save the tokens extracted from file
                        ArrayList<String> lst = new ArrayList<String>();

                        //tokenize the file, return the number of tokens of this document
                        int total = parser.Start(map, stopwordlist, System.out);

                        //add to mapping file
                        mapping.add(inputfile);

                        //copy local hash table to global hash table
                        Copy2Global(map, table, mapping.size() - 1, total);

                    }catch(java.io.FileNotFoundException e){
                        System.out.println(path + " not found.");
                        return ;
                    }
                }
            }
            writeDOcFixLength(table, mapping, output);
        }

        private static void Copy2Global(HashMap map, HashTable global, int docid, int numTokens){
            Set<String> keys = map.keySet();
            for(String key: keys){
                double f = (int)map.get(key) / (double)numTokens;
                if(global.containsKey(key)){
                    Entity entity = (Entity)global.getName(key);
                    entity.AddDocument(docid, f, (int)map.get(key));
                }else {
                    Entity entity = new Entity(docid, f, (int)map.get(key));
                    global.insert(key, entity);
                }
            }
            map.clear();
        }

        private static void writeDOcFixLength(HashTable global, ArrayList<String> mapping, File folder){
            int NumberDoc = mapping.size();
            try
            {
                File file = new File(folder, "mapping.txt");
                FileWriter fwriter=new FileWriter(file);
                for(int i = 0; i< mapping.size(); i++){
                    fwriter.write(formatString(mapping.get(i), 8) + "\u005cn");
                }
                fwriter.close();

                file = new File(folder, "dict.txt");
                fwriter = new FileWriter(file);

                File postfile = new File(folder, "post.txt");
                FileWriter writer = new FileWriter(postfile);

                int starts = 0;
                List<Document> docs;
                int lowfrequency = 0;
                for(int i=0; i< global.getSize(); i++){
                    String key = global.getNode(i).getAddr();
                    if(!key.equals("")){
                        Entity entity = (Entity)global.getName(key);
                        int Term_frequency = entity.Term_frequency;

                        if(Term_frequency > 1) {
                            int value = entity.Number_Doc;
                            fwriter.write(formatString(key, 10) + " " + formatString(String.valueOf(value), 5) + " " + formatString(String.valueOf(starts), 8) + "\u005cn");
                            starts += value;
                            docs = entity.documents;

                            double idf = Math.log((double) NumberDoc / docs.size());

                            for (Document doc : docs) {
                                writer.write(formatString(String.valueOf(doc.Document_id), 5) + " " + formatString(String.valueOf(doc.term_frequency * idf * 100), 4) + "\u005cn");
                            }
                        }
                        else {
                            fwriter.write(formatString("DELETED", 25) + "\u005cn");
                            lowfrequency++;
                        }
                    }
                    else{
                        fwriter.write(formatString("", 25) + "\u005cn");
                    }
                }
                fwriter.close();
                writer.close();
                System.out.println("The number of low frequency words are removed :" + lowfrequency);
            }
            catch(IOException e)
            {
                System.err.println("IOEror: "+e.getMessage());
            }
        }

        private static String formatString(String value, int length){
            if(value.length() > length){
                return value.substring(0, length);
            }else {
                String format = "%1$-" + length + "s";
                return String.format(format, value);
            }

        }


        private static class Entity{
            public int Number_Doc;
            public int Term_frequency;
            public List<Document> documents;

            public Entity(int document, double frequency, int term_frequency){
                Number_Doc = 1;
                documents = new ArrayList<Document>();
                documents.add(new Document(document, frequency));
                Term_frequency = term_frequency;
            }

            public void AddDocument(int doc, double frequency, int term_frequency){
                Number_Doc++;
                Term_frequency += term_frequency;
                documents.add(new Document(doc, frequency));

            }
        }

        private static class Document{
            public int Document_id;
            public double term_frequency;

            public Document(int id, double frequency){
                Document_id = id;
                term_frequency = frequency;
            }

        }

  final public int Start(HashMap map, HashMap stopwordlist, PrintStream printStream) throws ParseException {
    Token t;
    String key = null;
    int val;
    int total = 0;
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLURAL2:
      case EXCEPT:
      case VERB:
      case REPLACE:
      case PLURAL:
      case PAST:
      case REPLACE2:
      case STOPPING:
      case STRING:
      case OTHER:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EXCEPT:
      case STRING:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case STRING:
          t = jj_consume_token(STRING);
          break;
        case EXCEPT:
          t = jj_consume_token(EXCEPT);
          break;
        default:
          jj_la1[1] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
            //convert token to lowercase
            key = t.image.toLowerCase();
            if(!stopwordlist.containsKey(key)){
                val = 1;
                if(map.containsKey(key)){
                    val += (int)map.get(key);
                }
                map.put(key, val);
                total += 1;
            }
        break;
      case PLURAL:
        t = jj_consume_token(PLURAL);
            key = t.image.toLowerCase();

            //if the token is plural, then remove the 's'
            key = key.substring(0, key.length() - 1);
            if(!stopwordlist.containsKey(key)){
                val = 1;
                if(map.containsKey(key)){
                    val += (int)map.get(key);
                }
                map.put(key, val);
                total += 1;
            }
        break;
      case PLURAL2:
      case VERB:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case VERB:
          t = jj_consume_token(VERB);
          break;
        case PLURAL2:
          t = jj_consume_token(PLURAL2);
          break;
        default:
          jj_la1[2] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
            key = t.image.toLowerCase();

            //if the token ends with 's or s' to display verb, then remove them
            //if the token ends with 'sses', then remove 'es'
            key = key.substring(0, key.length() - 2);
            if(!stopwordlist.containsKey(key)){
                val = 1;
                if(map.containsKey(key)){
                    val += (int)map.get(key);
                }
                map.put(key, val);
                total += 1;
            }
        break;
      case PAST:
        t = jj_consume_token(PAST);
            key = t.image.toLowerCase();
            //if one token starts with one vowel and follow one non-vowel, also it ends with 'eed' or 'eedly'.
            //it means this token should have more than 5 characters
            //then remove 'eed' or 'eedly' to 'ee'
            if(key.length() > 5){
                key = key.replaceAll("(eedly|eed)$", "ee");
            }
            if(!stopwordlist.containsKey(key)){
                val = 1;
                if(map.containsKey(key)){
                    val += (int)map.get(key);
                }
                map.put(key, val);
                total += 1;
            }
        break;
      case REPLACE:
        t = jj_consume_token(REPLACE);
            key = t.image.toLowerCase();
            //replace 'ied' or 'ies' to 'ie' when the token is less than 5 characters, otherwise replace to 'i'
            String param = key.length() < 5 ? "ie" : "i";
            key = key.replaceAll("(ied|ies)$",param);
            if(!stopwordlist.containsKey(key)){
                val = 1;
                if(map.containsKey(key)){
                    val += (int)map.get(key);
                }
                map.put(key, val);
                total += 1;
            }
        break;
      case REPLACE2:
        t = jj_consume_token(REPLACE2);
            key = t.image.toLowerCase();
            if(key.length() > 4){

                //remove 'ed', 'edly', 'ing', 'ingly' when the token contains one vowel and longer than 4.
                key = key.replaceAll("(ed|edly|ing|ingly)$", "");

                //after removing, if the token ends with 'at', 'bl' or 'iz', then add 'e'
                if(key.matches("^.+?(at|bl|iz)$")){
                    key = key + "e";
                }
                //or if the token ends with double letter, but not 'll', 'ss' or 'zz', then remove the last letter
                if(key.substring(key.length()- 2).matches("([a-z])\u005c\u005c1$") && key.matches("^.+?[^ll|ss|zz]$")){
                    key = key.substring(0, key.length() - 1);
                }
            }
            if(!stopwordlist.containsKey(key)){
                val = 1;
                if(map.containsKey(key)){
                    val += (int)map.get(key);
                }
                map.put(key, val);
                total += 1;
            }
        break;
      case OTHER:
        t = jj_consume_token(OTHER);
        break;
      case STOPPING:
        t = jj_consume_token(STOPPING);
        break;
      default:
        jj_la1[3] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    jj_consume_token(0);
      {if (true) return total;}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public indexTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[4];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x7fc00000,0x40800000,0x1400000,0x7fc00000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x400,0x0,0x0,0x400,};
   }

  /** Constructor with InputStream. */
  public index(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public index(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new indexTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public index(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new indexTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public index(indexTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(indexTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[43];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 4; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 43; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

    }
