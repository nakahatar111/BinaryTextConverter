import java.util.StringTokenizer;
import javax.swing.text.html.BlockView;

public class converter{
  public static void main(String[] args)
	{
		// put some code here to check for three commandline arguments
		if(args.length != 3){
      System.out.println("format: nakahata_lab2 \"type\" \"input file\" \"output file\"");
      System.exit(0);
    }
    if((!args[0].toLowerCase().startsWith("b")) && (!args[0].toLowerCase().startsWith("t"))){
      System.out.println("The first argument must start with a \"b\" or a \"a\"");
      System.exit(0);
    }
		// puts some code here to check that the first commandline argument starts with "b" or "t"
		if( args[0].startsWith("b") ){
			convertBinaryToText(args[1], args[2]);
		}
		else{
			convertTextToBinary(args[1], args[2]);
		}
	}

	private static void convertBinaryToText(String inputFilename, String outputFilename){
		System.out.println("convertBinaryToText");
		try{
      java.io.BufferedInputStream input = new java.io.BufferedInputStream(new java.io.FileInputStream(inputFilename));
      java.io.PrintWriter output = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(outputFilename)));

      byte[] byteArray = new byte[20];
      java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.wrap(byteArray);
      input.read(byteArray,0,4);
      int numberOfBlocks = byteBuffer.getInt(0);

      for( int i = 0; i < numberOfBlocks; i++ ){
        byteBuffer.clear();
        input.read(byteArray,0,2);
        char blockType = byteBuffer.getChar(0);

        if(blockType == 's'){
          String result = "";
          input.read(byteArray,0,4);
          int length = byteBuffer.getInt(0);

          for(int j = 0; j < length; j++){
            input.read(byteArray,0,2);
            char s_data = byteBuffer.getChar(0);
            result += s_data;
          }
          output.write("string\t" + result);
        }
        else if(blockType == 'd'){
          input.read(byteArray,0,8);
          double d_data = byteBuffer.getDouble(0);
          output.write("double\t" + d_data);
        }
        else if(blockType == 'i'){
          input.read(byteArray,0,4);
          int i_data = byteBuffer.getInt(0);
          output.write("int\t" + i_data);
        }
        else if(blockType == 'f'){
          input.read(byteArray,0,4);
          float f_data = byteBuffer.getFloat(0);
          output.write("float\t" + f_data);
        }
        else if(blockType == 'l'){
          input.read(byteArray,0,8);
          long l_data = byteBuffer.getLong(0);
          output.write("long\t" + l_data);
        }
        else if(blockType == 'h'){
          input.read(byteArray,0,2);
          Short h_data = byteBuffer.getShort(0);
          output.write("short\t" + h_data);
        }
        else if(blockType == 'a'){
          input.read(byteArray,0,4);
          int length = byteBuffer.getInt(0);
          String[] arr = new String[length];

          for(int j = 0; j < length; j++){
            input.read(byteArray,0,4);
            arr[j] = String.valueOf(byteBuffer.getInt(0));
          }
          String int_a_data = String.join(",", arr);
          output.write("int array\t" + int_a_data);
        }
        output.write('\n');
      }
      output.close();
      input.close();
    }
		catch(Exception e)
		{
			System.out.println(e.toString());
			System.exit(0);
		}
	}
	

	private static void convertTextToBinary(String inputFilename, String outputFilename){
		System.out.println("convertTextToBinary");
		try{
      java.io.BufferedReader input = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(inputFilename)));
			java.io.BufferedOutputStream output = new java.io.BufferedOutputStream(new java.io.FileOutputStream(outputFilename));
      java.util.ArrayList<String> inputLines = new java.util.ArrayList<>(0);
      
      //count line
      int count = 0;
      String inn;
      while((inn = input.readLine()) != null){
        count++;
        inputLines.add(inn);
      }
      
      byte[] byteArray = new byte[200];
      java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.wrap(byteArray);
      byteBuffer.putInt(0, count);
      output.write(byteArray,0,4);

      for(String lines : inputLines){
        byteBuffer.clear();
        StringTokenizer st = new StringTokenizer(lines);
        String str = st.nextToken();

        if(str.equals("int")){
          String next_val = st.nextToken();

          if(next_val.equals("array")){
            byteBuffer.putChar(0, 'a');
            String[] int_arr = st.nextToken().split(",");
            byteBuffer.putInt(2, int_arr.length);
            output.write(byteArray,0,6);

            if(byteArray.length < int_arr.length*4){
              byteArray = new byte[int_arr.length*4];
              byteBuffer = java.nio.ByteBuffer.wrap(byteArray);
            }

            for(int i = 0; i < int_arr.length; i++){
              byteBuffer.putInt(i*4, Integer.parseInt(int_arr[i]));
            }
            output.write(byteArray,0,4*int_arr.length);
          }
          else{
            byteBuffer.putChar(0, 'i');
            byteBuffer.putInt(2, Integer.parseInt(next_val));
            output.write(byteArray,0,6);
          }
        }
        else if(str.equals("long")){
          byteBuffer.putChar(0, 'l');
          byteBuffer.putLong(2, Long.parseLong(st.nextToken()));
          output.write(byteArray,0,10);
        }
        else if(str.equals("short")){
          byteBuffer.putChar(0, 'h');
          byteBuffer.putShort(2, Short.parseShort(st.nextToken()));
          output.write(byteArray,0,4);
          
        }
        else if(str.equals("float")){
          byteBuffer.putChar(0, 'f');
          byteBuffer.putFloat(2, Float.parseFloat(st.nextToken()));
          output.write(byteArray,0,6);
          
        }
        else if(str.equals("double")){
          byteBuffer.putChar(0, 'd');
          byteBuffer.putDouble(2, Double.parseDouble(st.nextToken()));
          output.write(byteArray,0,10);
        }
        else{
          byteBuffer.putChar(0, 's');
          output.write(byteArray,0,2);
          
          String data = "";
          while(st.hasMoreTokens()){
            data += st.nextToken() + " ";
          }
          char[] charArray = data.toCharArray();

          byteBuffer.putInt(0, data.length());
          output.write(byteArray,0,4);

          if(byteArray.length < charArray.length*2){
            byteArray = new byte[charArray.length*2];
            byteBuffer = java.nio.ByteBuffer.wrap(byteArray);
          }

          for( int j = 0; j < charArray.length; j++ ){
            byteBuffer.putChar(j*2, charArray[j]);
          }
          output.write(byteArray,0,2*charArray.length);
        }
      }
      output.close();
      input.close();
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
			System.exit(0);
		}
	}
}
