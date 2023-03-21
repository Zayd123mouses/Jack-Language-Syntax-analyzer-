import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.FileNotFoundException;
/**
 * JackAnalyzer
 */

//  the main class that drive the process of compiling jack files
public class JackAnalyzer {
    static private ArrayList<File> files = new ArrayList<File>();  /** list of files of type Xxx.jack 
     * @throws FileNotFoundException*/
    
    public static void main(String[] args) throws FileNotFoundException,Exception {
        File input = new File(args[0]); // get the input as a pathname
        getFiles(input);  // fill the files list with the proper files
        

        // for each file in the list of files
        for(File f_input: files){
            JackTokenizer jackTokenizer = new JackTokenizer(f_input); // genreating the tokens
            File output = getOutputFile(f_input,"Tme");               //create an output file with the name fileNameT.xml
            PrintWriter writeout = new PrintWriter(output);     //to save the tokins
            writeout.println("<tokens>");

            while(jackTokenizer.HasMoreToken()){
                jackTokenizer.advance(); //get the current token

                String classification = jackTokenizer.Classification(); // type of the token
                String valueOfToken = jackTokenizer.ValueOfTheCurrentToken(); // the value of the token
                
                writeout.print("<" + classification + ">"); //print in new line the type
                writeout.print(" " + valueOfToken + " ");     // print in the same line the value with white spaces
                writeout.print("</" +classification + ">");   // close the xml tag
                writeout.println();
            } //while
            writeout.println("</tokens>"); //closing tag of the file
            writeout.close(); // close the printwriter of tokenizer
            
            
           jackTokenizer.Reset(); //reset the tokenizer to 0 index;
           jackTokenizer.advance(); // set the current token

            File compilationOutput = getOutputFile(f_input, "Final"); //create new file to write compile xml
            new CompilationEngine(compilationOutput,jackTokenizer); // create new compilation engion to write compiled code

            
        } // for

     System.out.println("Done successfully");

    } // main

   
    
    // Add files of type jack to files list
    private static void getFiles(File input) throws FileNotFoundException {
        if(input.isDirectory()){
           File[] innerFiles = input.listFiles(); //get all the files in the directory
           for(File f: innerFiles){
            getFiles(f);
           }
        }else if(input.isFile()){
            String fileName = input.getName();  //get the name of the file
            int index = fileName.indexOf("."); // get the index of the "." in the string
            if(fileName.substring(index + 1).equals("jack") ){ // check that the file has extension "jack"
                files.add(input);
             }
        }else{
            throw new FileNotFoundException("Could not find file or directory.");
        }
    }



    // produce a new file with a new name
    private static File getOutputFile(File input,String replce) {
        String inputName = input.getName();
        int index  = inputName.indexOf(".");
        String outputName = inputName.substring(0, index) + replce + ".xml";

        return new File(input.getAbsolutePath().replaceAll(inputName, outputName)); // new output file with name xxxTme.xml
        
    }




    
} // class
    
