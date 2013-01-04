   import java.io.*;

/**
 *  UNRLE.java<P>
 *
 * Ported to Java by Tim Tyler, July 2001 - http://mandala.co.uk/birle/<P>
 * Slight modifications were made during the port.
 *
 * Modifeid by David A. Scott
 * see comment there for info.
 * As always if you choose to use this your on you own.
 *
 * Original code:
 *  Mark Nelson<BR>
 *  March 8, 1996<BR>
 *  http://web2.airmail.net/markn<BR>
 * 
 */
   public class UNRLE {
      final static boolean report_times = true;
      final static boolean report_size  = true;
   
      FileOutputStream out = null;
      FileInputStream in = null;
      byte[] array = new byte[1];
   
      void putc(int i) throws IOException {
         out.write((byte)i);
      }
   
   
      int getc() throws IOException {
         int rb;
      
         if ((rb = in.read(array, 0, 1)) == -1) {/* Read the next character. */
            return -1; // EOF flag...
         }
      
         return array[0] & 0xff;
      }
   
   
   /**
    * Decompresses input_file and stores the results in output_file
    */
      public void decompress(String input_file, String output_file) throws IOException {
         long start_time;
      
         if (report_times) {
            start_time = System.currentTimeMillis();
         }
      
         try {
            in = new FileInputStream(input_file);
            out = new FileOutputStream(output_file);
         
            int last = -1; // see notes in RLE.java
            int lastbo = -2; // see notes in RLE.java
         
            int c;
            int count;
            while ((c = getc()) >= 0)  {
               putc(c);
               if (lastbo == last) {
                  if (c == last) {
                     count = getc();
                     if (count < 0) {
                        break; /* EOF specail end */
                     }
                  
                     while ((c = getc()) == last) {
                        count += 256;
                     }
                  
                     if (c < 0) {
                        count += (count % 256) == 0 ? 256: 0;
                     }
                  
                     while (count-- > 0) {
                        putc(last);
                     }
                  
                     if (c < 0) {
                        break; /* more EOF processing */
                     }
                  
                     putc(c);
                  }
               }
            
               lastbo = last;
               last = c;
            }
         
            in.close();
            out.close();
         }
            catch(Exception e) {
               printf("\nError while processing file:");
               e.printStackTrace();
            }
      
         if (report_times) {
            start_time = System.currentTimeMillis() - start_time;
            printf("Time taken:" + ((start_time / 10) / 100F) + " seconds.");
         }
      
         if (report_size) {
            File f1 = new File(input_file);
            printf("Original file size:   " + f1.length() + " bytes.");
            File f2 = new File(output_file);
            printf("Compressed file size: " + f2.length() + " bytes.");
         }
      }
   
   
      static void printf(String s) {
         System.out.println(s);
      }
   
   
   /**
    * Handles the Commmand-line interface to the compressor
    */
      public static void main(String[] args) throws IOException {
         UNRLE da = new UNRLE();
      
         String input_file = null;
         String output_file = null;
      
         printf("Bijective RLE decompressor version of July 12, 2001.");
         // printf("Arithmetic decoding from ");
      
         if (args.length > 0) {
            input_file = args[0];
            printf("Compressed file: " + args[0]);
         } 
         else {
            printf("Error: No input file specified");
            System.exit(0);
         }
      
         if (args.length > 1) {
            output_file = args[1];
            printf("Decompressed file: " + args[1]);
         } 
         else {
            printf("Error: No output file specified");
            System.exit(0);
         }
      
         da.decompress(input_file, output_file);
      }
   
   }