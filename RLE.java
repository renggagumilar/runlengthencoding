   import java.io.*;

/**
 * Ported to Java by Tim Tyler, July 2001 - http://mandala.co.uk/birle/<P>
 *
 * I modified the program as follows:<BR>
 * + No longer sensitive" to leading zeros in the file.<BR>
 * + Now required three consecutive characters (rather than two)
 *   before RLE coding kicks in.<P>
 *
 * David Scott's comments follow:<P>
 * 
 * This has been changed to be bijective and unadulterated
 * meaning that for any file X then compress(uncompress(X))=X
 * and uncompress(compress(X))=X.  This was not the case in
 * the earlier version by Mark.<P>
 *
 * This mode is done by David A. Scott 2000 Jan 5.<P>
 * Use at your own risk.<P>
 * I got it to work using DJGPP GNU C port to DOS.<P>
 *
 * The main changes are the EOF treatment and the fact
 * that very long repeats in the old version are of form
 * aa255a255a255aN where the a255 repeated for as many times as needed
 * the undulterated form of this kind of exansion is
 * aaNaaaa where the trailing a's represent 256 a's.<P>
 *
 * These changes are such that the method will always compress
 * as well as the old RLE or better. In fact for some files with
 * very long repeats it will compress twice as good.
 * the first savings occur at repeat of 258 as shown in
 * example:<P>
 * baaaa..<258 a's total>...aac<P>
 * OLD RLE<P>
 * baa255a0c<P>
 * NEW RLE<P>
 * baa0ac<P>
 *
 * In the old method aa0b and aa0a have same meaning or the
 * aa0a form not really used since aa1 is same thing. What the
 * old method failed to do was make wise use of the patterns
 * so that information is being added to the file as it compresses
 * so it should not be used if one is encyrpting since a unadulterated
 * RLE is available that always matches or beats the old RLE method.<P>
 *
 * Another example more obvious:<P>
 * take the compressed file<P>
 * aa3a3bc<P>
 * this when uncompressed and recompressed old way becomes<P>
 * aa7bc <P>
 * which means the first file could not be a result of
 * the current RLE compression meaning if it was the result
 * of guessing a key for encryption that key used is wrong.<P>
 * One should avoid use compression methods that aid someone
 * in breaking the code.<P>
 *
 * In the new method<P>
 * aa3abc<P>
 * when uncompressed and rcompressed back comes back as same file.<P>
 *
 *
 *  RLE.CPP<P>
 *
 *  Mark Nelson<P>
 *  March 8, 1996<P>
 *  http://web2.airmail.net/markn<P>
 *
 * DESCRIPTION<BR>
 * -----------<P>
 *
 *  This program performs a Run Length Encoding function on an
 *  input file/stream, and sends the result to an output file
 *  or stream.  In the output stream, any two consecutive
 *  characters with the same value flag a run.  A byte following
 *  those two characters gives the count of *additional*
 *  repeat characters, which can be anything from 0 to 255.<P>
 *
 *  Using the RLE program as a front end to BWT avoids
 *  pathologically slow sorts that occur when the input stream
 *  has long sequences of identical characters. (Which means
 *  comparison functions have to spend lots of time on a pair
 *  of strings before deciding who is larger.)<P>
 *
 *  This program takes two arguments: an input file and an output
 *  file.  You can leave off one argument and send your output to
 *  stdout.  Leave off two arguments and read your input from stdin
 *  as well.<P>
 *
 *  This program accompanies my article "Data Compression with the
 *  Burrows-Wheeler Transform."<P>
 *
 * Build Instructions<BR>
 * ------------------<P>
 *
 *  Define the constant unix for UNIX or UNIX-like systems.  The
 *  use of this constant turns off the code used to force the MS-DOS
 *  file system into binary mode. g++ already does this, your UNIX C++
 *  compiler might also.<P>
 *
 *  Borland C++ 4.5 16 bit    : bcc -w rle.cpp<BR>
 *  Borland C++ 4.5 32 bit    : bcc32 -w rle.cpp<BR>
 *  Microsoft Visual C++ 1.52 : cl /W4 rle.cpp<BR>
 *  Microsoft Visual C++ 2.1  : cl /W4 rle.cpp<BR>
 *  g++                       : g++ -o rle rle.cpp<P>
 *
 * Typical Use<BR>
 * -----------<P>
 *
 *  rle < raw-file | bwt | mtf | rle | ari > compressed-file<P>
 *
 */

   public class RLE {
   
      final static boolean report_times = true;
      final static boolean report_size  = true;
      final static boolean report_filenames  = true;
   
      FileOutputStream out = null;
      FileInputStream in = null;
      byte[] array = new byte[1];
   
   /**
    * Compresses input_file and store the results in output_file
    */
      public void compress(String input_file, String output_file) throws IOException {
         long start_time;
      
         if (report_filenames) {
            printf("Original file:   " + input_file);
            printf("Compressed file: " + output_file);
         }
      
         if (report_times) {
            start_time = System.currentTimeMillis();
         }
      
         int zerf = 0;
         int onef = 0;
      
         try {
            in = new FileInputStream(input_file);
            out = new FileOutputStream(output_file);
         
            byte[] _array = new byte[1];
         
            int rb;
         
            int last = -1;
            int lastbo = -2;
            // int lastbt = -3; // probably hinders rather than helps
         
         /* means already first characater was a zero */
         /* I would change this to -1 since you could have a file with
            a single leading zero. If you do you get an extra zero with his
            value of zero. I am not changeing this to -1 here so that I will
            exactly match his method for a files where the variable "count" is
            255 or less. So except for the possible saveing of a byte at
            end of file our outputs files will match for many cases */
            int c;
            while ((c = getc()) != -1) {/* Read the next character. */
               putc(c);  // output it...
               // if (lastbt == lastbo) {
               if (lastbo == last) {
                  if (c == last) {
                     int count = 0;
                     while (( c = getc()) >= 0 ) {
                        if (c == last) {
                           count++;
                        }
                        else
                        {
                           break;
                        }
                     }
                  
                     if ( c < 0 ) { /* EOF handling */
                        if ((count >= 256) && (count % 256 == 0)) {
                           count -=256; 
                        }
                        else {
                           if ( count == 0 ) count = -1;
                        }
                     }
                     if (count >=0) {
                        putc(count);
                     }
                  
                     while ((count = count - 256) >= 0) {
                        putc(last);
                     }
                  
                     if (c >= 0) {
                        putc(c);
                     }
                  }
               }
               // }
            
               // lastbt = lastbo;
               lastbo = last;
               last = c;
            }
         
            in.close();
            out.close();
         }
            catch(Exception e) {
               printf("Error while processing file:");
               e.printStackTrace();
            }
      
         if (report_times) {
            start_time = System.currentTimeMillis() - start_time;
            printf("Time taken:" + ((start_time / 10) / 100F) + " seconds.");
         }
      
         if (report_size) {
            File f1 = new File(input_file);
            File f2 = new File(output_file);
         
            long f1l = f1.length();
            long f2l = f2.length();
         
            printf("Original file size:   " + f1l + " bytes.");
         
            printf("Compressed file size: " + f2l + " bytes.");
            if (f1l > f2l) {
               printf("Compressed by " + (f1l - f2l) + " bytes.");
            }
            else
            {
               printf("*** File gained in size by " + (f2l - f1l) + " bytes. ***");
            }
         
         	// fudge divide by zero?
            if (f1l == 0L) {
               f1l = 1L;
            }
         
            printf("Compression ratio:    " + ((((f1l - f2l) * 10000) / f1l) / 100F) + "%");
         
         }
      
         printf("");
      }
   
   
      int getc() throws IOException {
         int rb;
      
         if ((rb = in.read(array, 0, 1)) == -1) {/* Read the next character. */
            return -1; // EOF flag...
         }
      
         return array[0] & 0xff;
      }
   
   
      void putc(int i) throws IOException {
         out.write((byte)i);
      }
   
   
      static void printf(String s) {              
         System.out.println(s);
      }
   
   /**
    * Handles the Commmand-line interface to the compressor
    */
      public static void main(String[] args) throws IOException {
         RLE ca = new RLE();
      
         String input_file = null;
         String output_file = null;
      
         printf("Bijective RLE compressor version of July 12, 2001.");
      
         if (args.length > 0) {
            input_file = args[0];
         } 
         else {
            printf("Error: No input file specified");
            System.exit(0);
         }
      
         if (args.length > 1) {
            output_file = args[1];
         } 
         else {
            printf("Error: No output file specified");
            System.exit(0);
         }
      
         ca.compress(input_file, output_file);
      }
   
   }