import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;


/**
 * CompilationEngine
 */
// class to use jack tokenizer and write compiled tokens into an output file called xxxFinal.xml
public class CompilationEngine {
   
    private PrintWriter writeout;
    private JackTokenizer jackTokenizer;

    public CompilationEngine(File A_output,JackTokenizer A_jackTokenizer) throws Exception ,FileNotFoundException{
        writeout = new PrintWriter(A_output);
        jackTokenizer = A_jackTokenizer;
        CompileClass();
    }
    // compile a complete class
    private void CompileClass() throws Exception{
     writeout.println("<class>");
     eat("class"); 
     eat("identifier");
     eat("{");
     // compile all class variable declaration
     while(jackTokenizer.HasMoreToken() && jackTokenizer.TokenType() == 1 && (jackTokenizer.KeyWord() == 16 || jackTokenizer.KeyWord() == 15)){
        CompileClassVarDec();
     }
     // compile all subroutines
     while(jackTokenizer.HasMoreToken() && jackTokenizer.TokenType() == 1 && (jackTokenizer.KeyWord() == 12 || jackTokenizer.KeyWord() == 13 || jackTokenizer.KeyWord() == 14)){
        CompileSubRoutine();
     }
     eat("}");
     writeout.println("</class>"); //end tag
     writeout.close();
    }
    // compile class static type or field variable decleration
    private void CompileClassVarDec() throws Exception{
        writeout.println("<classVarDec>");
       //field or static 
       if(jackTokenizer.KeyWord() == 15){
        eat("field");
       }else if(jackTokenizer.KeyWord() == 16){
        eat("static");
       } 

        // handle the type of the var
        if(jackTokenizer.TokenType() == 1){
            eat("keyword");
        }else{
            eat("identifier");
        }
         
        // handle multible var in same line
        while(true){
        eat("identifier");
        if(jackTokenizer.TokenType() == 2 && jackTokenizer.Symbol().equals(";")){
            eat(";");
            break;
        }
        eat(",");
    }
        writeout.println("</classVarDec>");

    }
     
    // compile a complete method or a function 
    private void CompileSubRoutine() throws Exception{
      writeout.println("<subroutineDec>");
      eat("keyword");
      if(jackTokenizer.TokenType() == 1) // if the type is a keyword (boolean, char, int)
      {
        eat("keyword");
      }else{
        eat("identifier"); // the type is a className type
      }

      eat("identifier"); // the name of the subRoutine
      eat("(");
      CompileParamList();   
      eat(")");
      CompileSubRoutineBody();
      writeout.println("</subroutineDec>");
    }
    // compile subroutine body including the enclosing {}
    private void CompileSubRoutineBody() throws Exception{
        writeout.println("<subroutineBody>"); // beginning of the body tag
        eat("{");

        // handle multible variable declarations 
        while(jackTokenizer.TokenType() == 1 && jackTokenizer.KeyWord() == 17){
            CompileVarDec();
        }
        CompileStatment(); //compile statments

        eat("}");
        writeout.println("</subroutineBody>"); // beginning of the body tag
    }
    // compile var declaration inside function or method
    private void CompileVarDec() throws Exception{
        //var int x,y;
        // var int x;
        writeout.println("<varDec>");

        eat("var");
        // handle the type of the var
        if(jackTokenizer.TokenType() == 1){
            eat("keyword");
        }else{
            eat("identifier");
        }
         
        // handle multible var in same line
        while(true){
        eat("identifier");
        if(jackTokenizer.TokenType() == 2 && jackTokenizer.Symbol().equals(";")){
            eat(";");
            break;
        }
        eat(",");
    }

        writeout.println("</varDec>");

    }
    // compile the possiable empty paramater list
    // dose not handle the enclosing "()"
    private void CompileParamList() throws Exception{
        writeout.println("<parameterList>"); // beginning of the tag 
        
          while(jackTokenizer.TokenType() != 2){
            if(jackTokenizer.TokenType() == 1){ // if it is a key word (int , char, boolean)
                eat("keyword");             
            }else{
                eat("identifier"); // handle type className
            }

            eat("identifier"); // handle the variable name
            
            // if reached the end of the param list then break
            if(jackTokenizer.TokenType() == 2 && jackTokenizer.Symbol().equals(")")){
                break;
            }
            eat(",");   // handle multible params
          }
        writeout.println("</parameterList>"); //end tag for param list

    }
    // compile a sequance of statments dose not handle the enclosing {}
    private void CompileStatment() throws Exception{
        writeout.println("<statements>");
        
        List<Integer> tokentypes = List.of(115,116,117,119,120); // type of the statement
        while(jackTokenizer.TokenType() == 1 && tokentypes.contains(jackTokenizer.KeyWord())){
        int tokentype = jackTokenizer.KeyWord();  //asumming the current token is a keyword
        switch (tokentype) {
            case 115:      // let 
               CompileLet();   
                break;
            case 116:    // do
                CompileDo();   
                break;
            case 117:   // if
                CompileIf();   
                break;
            case 119:   // while
                CompileWhile();   
                break; 
            default:   // return
                CompileReturn();
                break;
        }
    } // while

    writeout.println("</statements>");

    }
    // compile if statment
    private void CompileIf() throws Exception{
        writeout.println("<ifStatement>"); // the start tag for IF 
        eat("if");
        eat("(");
        CompileExpression();
        eat(")");
        eat("{");
        CompileStatment();
        eat("}");
       
        // handle else statement
        if(jackTokenizer.ValueOfTheCurrentToken().equals("else")){
            eat("else");
            eat("{");
            CompileStatment();
            eat("}");
        }
        writeout.println("</ifStatement>"); // the start tag for IF 


    }
    // compile While statment
    private void CompileWhile() throws Exception{

      writeout.println("<whileStatement>"); // the start tag for while 
      eat("while");
      eat("(");
      CompileExpression();
      eat(")");
      eat("{");
      CompileStatment();
      eat("}");
      writeout.println("</whileStatement>"); // the end tag for while


    }
    // Compile do statemnts
    private void CompileDo() throws Exception {
        writeout.println("<doStatement>"); //begining of the do statment
        eat("do");  // code handle do
        eat("identifier"); // handle identifier

        if(jackTokenizer.ValueOfTheCurrentToken().equals(".")){ // check if the next token is "."
            eat(".");
            eat("identifier");
        }
            eat("(");
            CompileExpressionList();
            eat(")");
            eat(";");
          writeout.println("</doStatement>"); //begining of the do statment
    }

    //Compile let statement
    private void CompileLet() throws Exception {
        writeout.println("<letStatement>"); //begining of the let statment
        eat("let");
        eat("identifier");
        if(jackTokenizer.Symbol().equals("[")){ // if the token is [
            eat("[");
            CompileExpression();
            eat("]");
        }
        eat("=");
        CompileExpression();
        eat(";"); 
        writeout.println("</letStatement>"); //end of the let statment

    }
    //compile Expression
    private void CompileExpression() throws Exception{
        writeout.println("<expression>");

        CompileTerm(); // handle term
        // op tearn
        List<String> OP = List.of("+","-","*","/","&amp;","|","&gt;","&lt;","=");
        // handle multible terms 
        while(OP.contains(jackTokenizer.Symbol())){
            eat("symbol");
            CompileTerm();
        }
        writeout.println("</expression>"); // end tag for expression

    }
    //compile Term  
    private void CompileTerm() throws Exception{
        writeout.println("<term>");
        switch (jackTokenizer.TokenType()) {
            case 3:          //  handle the term is  identifier

            eat("identifier");
            if(jackTokenizer.TokenType() == 2){
            switch (jackTokenizer.Symbol()) {
                case "[":            // handle list 
                    eat("[");
                    CompileExpression();
                    eat("]");
                    break;
                case "(":   //handle subroutne call
                    eat("(");
                    CompileExpressionList();
                    eat(")");
                    break;
                case ".":           // handle subroutine call
                    eat(".");
                    eat("identifier");
                    eat("(");
                    CompileExpressionList();
                    eat(")");
                    break;
            }
          }
              break;
            
            case 4: // handle integer constant
                eat(jackTokenizer.IntVal() + "");
                break;
            case 5: // handle string constant
                eat(jackTokenizer.StringVal());
                break;
            case 1: // handle keyword contant true, false, null and this
              eat("keyword");
              break;
            case 2: // handle symbols (), ~ and -
                if(jackTokenizer.Symbol().equals("(")){
                    eat("(");
                    CompileExpression();
                    eat(")");
                   
                }else if(jackTokenizer.Symbol().equals("~")){
                    eat("~");
                    CompileTerm();
                    
                }else if(jackTokenizer.Symbol().equals("-")){
                    eat("-");
                    CompileTerm();
                    
                }
              break;

        }
        writeout.println("</term>");

    }
    // compile list of expression
    private void CompileExpressionList() throws Exception{
        writeout.println("<expressionList>");
        
        while(!jackTokenizer.Symbol().equals(")")){  // while not )

            CompileExpression();
            
            // if reached the end of the param list then break
            if(jackTokenizer.TokenType() == 2 && jackTokenizer.Symbol().equals(")")){
                break;
            }
            eat(",");   // handle multible expressions
          }

        writeout.println("</expressionList>");

    }

    private void CompileReturn() throws Exception {
        writeout.println("<returnStatement>"); //begining of the return statment
        eat("return");
        if(jackTokenizer.ValueOfTheCurrentToken().equals(";")){
            eat(";");
        }else{
            CompileExpression();
            eat(";");
        }
        writeout.println("</returnStatement>"); //end  of the do statment
        
    }

    private void eat(String expected) throws Exception{
        String valOfToken = jackTokenizer.ValueOfTheCurrentToken();
        String classfication  = jackTokenizer.Classification();

      if(expected.equals(valOfToken) || expected.equals(classfication)){
          writeout.print("<" + classfication + "> ");
          writeout.print(valOfToken);
          writeout.print(" </" + classfication + ">");
          writeout.println();
          if(jackTokenizer.HasMoreToken()){
          jackTokenizer.advance();
          }
      }else{
        System.out.println(classfication);
        throw new Exception("Error read:  " + expected + " /the token  " + jackTokenizer.ValueOfTheCurrentToken()); 
      }
    }
}
