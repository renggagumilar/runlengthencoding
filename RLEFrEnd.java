/*
 * ToDo:
 *
 *
 */

   import java.awt.*;
   import java.applet.*;
   import java.awt.event.*;
   import java.net.URL;
   import java.io.*;
   import java.util.zip.*;

/**
 * RLEFrEnd - Tim Tyler 2001.
 * 
 * A front end for RLE.<P>
 * This code has been placed in the public domain.<P>
 * You can do what you like with it.<P>
 * Note that this code comes with no warranty.<P>
 *
 */
   public class RLEFrEnd extends java.applet.Applet implements ActionListener {
      static String document_base;
      static String java_version;
      static String directory_separator;
      static URL base_url;
   
      static Applet applet;
   
      static Panel input, output, input1, input2, input3;
      static Panel output1, output2, output3, output4, output5;
   
      static Frame frame;
   
      static boolean application = false;
   
      final static boolean development_version = false;
   
      static int i=0, j=0, k=0, n=0;
   
      static Checkbox checkbox_iv;
   
      static TextField original_file_textfield;
      static TextField compressed_file_textfield;
      static TextField decompressed_file_textfield;
   
      static TextField key_textfield;
   
      static Button original_file_button;
      static Button compressed_file_button;
      static Button decompressed_button;
   
      static Button encrypt_button;
      static Button decrypt_button;
   
   // constructor
      public RLEFrEnd() {
         setLayout(new BorderLayout());
         Font std_font = new Font("Helevetica", Font.BOLD, 16);
      
         output5 = new Panel();
         output5.setBackground(Color.lightGray);
      
         Label label_temp;
      
         Panel panel_basic = new Panel();
         panel_basic.setLayout(new BorderLayout());
      
         Panel panel_west = new Panel();
         panel_west.setLayout(new GridLayout(3,1));
         panel_west.setBackground(Color.lightGray);
      
         Panel panel_east = new Panel();
         panel_east.setLayout(new GridLayout(3,1));
      
         Panel panel_centre = new Panel();
         panel_centre.setLayout(new GridLayout(3,1));
         panel_centre.setBackground(Color.lightGray);
      
         label_temp = new Label("Original file:", Label.RIGHT);
         label_temp.setFont(std_font);
         panel_west.add(label_temp,BorderLayout.WEST);
         original_file_textfield = new TextField("",99);
         original_file_textfield.setFont(std_font);
         panel_centre.add(original_file_textfield,BorderLayout.CENTER);
         original_file_button = new Button("Browse");
         original_file_button.setFont(std_font);
         original_file_button.addActionListener(this);
         panel_east.add(original_file_button,BorderLayout.EAST);
      
         Panel panel_compressed_file = new Panel();
         label_temp = new Label("Compressed file:", Label.RIGHT);
         label_temp.setFont(std_font);
         panel_west.add(label_temp);
         compressed_file_textfield = new TextField("",99);
         compressed_file_textfield.setFont(std_font);
         panel_centre.add(compressed_file_textfield);
         compressed_file_button = new Button("Browse");
         compressed_file_button.setFont(std_font);
         compressed_file_button.addActionListener(this);
         panel_east.add(compressed_file_button);
      
         Panel panel_decompressed = new Panel();
         label_temp = new Label("Decompressed file:", Label.RIGHT);
         label_temp.setFont(std_font);
         panel_west.add(label_temp);
         decompressed_file_textfield = new TextField("",99);
         decompressed_file_textfield.setFont(std_font);
         panel_centre.add(decompressed_file_textfield);
         decompressed_button = new Button("Browse");
         decompressed_button.setFont(std_font);
         decompressed_button.addActionListener(this);
         panel_east.add(decompressed_button);
      
         output5.setLayout(new GridLayout(1,2));
      
         encrypt_button = new Button("Compress");
         encrypt_button.setFont(std_font);
         encrypt_button.addActionListener(this);
         output5.add(encrypt_button);
      
         decrypt_button = new Button("Decompress");
         decrypt_button.setFont(std_font);
         decrypt_button.addActionListener(this);
         output5.add(decrypt_button);
      
         panel_basic.add(panel_west,BorderLayout.WEST);
         panel_basic.add(panel_centre,BorderLayout.CENTER);
         panel_basic.add(panel_east,BorderLayout.EAST);
      
         add(panel_basic,BorderLayout.CENTER);
         add(output5,BorderLayout.SOUTH);
      }
   
   
      void enableButtons(boolean flag) {
         encrypt_button.setEnabled(flag);
         decrypt_button.setEnabled(flag);
      }
   
   
      public void actionPerformed(ActionEvent ev) {
         if (ev.getSource() == encrypt_button) {
            enableButtons(false);
            try {
               compress();
            }
               catch (Exception e) {
                  printf("Error while encrypting:");
                  e.printStackTrace();
               }
         
            enableButtons(true);
         }
      
         if (ev.getSource() == decrypt_button) {
            enableButtons(false);
            try {
               decompress();
            }
               catch (Exception e) {
                  printf("Error while decrypting:");
                  e.printStackTrace();
               }
         
            enableButtons(true);
         }
      
         if (ev.getSource() == compressed_file_button) {
            browseEncryptedFile();
         }
      
         if (ev.getSource() == decompressed_button) {
            browseDecryptedFile();
         }
      
         if (ev.getSource() == original_file_button) {
            browseOriginalFile();
         }
      }
   
   
      void getDocBase() {
         if (document_base == null) {
         
         	// get Java version...
            java_version = System.getProperty("java.version");
            directory_separator = System.getProperty("file.separator");
         
            if (application) {
               try {
                  document_base = System.getProperty("user.dir");
                  base_url = new URL("file:/" + document_base + directory_separator);
               }
                  catch (Exception e2) {
                     printf("Error getting application base: " + e2.toString());
                  }
            }
            else
            {
               try {
                  // base_url = getDocumentBase(); // applet
                  base_url = getCodeBase(); // applet
                  document_base = base_url.toString();
                  String base_url_s = base_url.toString(); // applet
               
                  // trailing "."...?
                  if (base_url_s.indexOf("/.") > 0) {
                     printf("Warning: slash-fullstop in URL - truncating URL to avoid bug.");
                     document_base = document_base.substring(0, document_base.length() - 2);
                     base_url = new URL(base_url_s.substring(0, base_url_s.length() - 1));
                  }
               }
                  catch (Exception e) {
                     printf("Error getting applet base: " + e.toString());
                  }
            }
         }
      }
   
   
      static void compress() throws Exception {
         RLE rle = new RLE();
         String original_file = original_file_textfield.getText();
         String compressed_file = compressed_file_textfield.getText();
         rle.compress(original_file, compressed_file);
      }
   
   
      static void decompress() throws Exception {
         UNRLE unrle = new UNRLE();
         String compressed_file = compressed_file_textfield.getText();
         String decompressed_file = decompressed_file_textfield.getText();
      
         unrle.decompress(compressed_file, decompressed_file);
      }
   
   
      static void browseOriginalFile() {
         FileDialog fd = new FileDialog(frame, "Select file", FileDialog.LOAD);
      
         fd.setFile("input.dat");
      
         fd.show();
         String returnedstring = fd.getFile();
         if (returnedstring != null) {
            if (!returnedstring.equals("")) {
               original_file_textfield.setText(fd.getDirectory() + returnedstring);
            }
         }
      }
   
   
      static void browseEncryptedFile() {
         FileDialog fd = new FileDialog(frame, "Select file", FileDialog.LOAD);
      
         fd.setFile("input.dat");
      
         fd.show();
         String returnedstring = fd.getFile();
         if (returnedstring != null) {
            if (!returnedstring.equals("")) {
               compressed_file_textfield.setText(fd.getDirectory() + returnedstring);
            }
         }
      }
   
   
      static void browseDecryptedFile() {
         FileDialog fd = new FileDialog(frame, "Select file", FileDialog.LOAD);
      
         fd.setFile("output.dat");
      
         fd.show();
         String returnedstring = fd.getFile();
         if (returnedstring != null) {
            if (!returnedstring.equals("")) {
               decompressed_file_textfield.setText(fd.getDirectory() + returnedstring);
            }
         }
      }
   
   
      static void printf(String s) {                            
         System.out.println(s);
      }
   
   
      public static void main(String args[]) {
         application = true;
      
         RLEFrEnd applet = new RLEFrEnd();
      
         frame = new TTAppletFrame("BIRLE",(Applet)applet,512,150);
         Image image = frame.createImage(29,29);
         Graphics g = image.getGraphics();
         g.setColor(Color.black);
         g.fillRect(0,0,30,30);
         g.setColor(Color.yellow);
         g.drawRect(0,0,28,28);
      
         for (int i = 1; i < 14; i++) {
            int temp = i * 14;
            g.setColor(new Color((temp << 12) | (temp ^ 0xFF)));
         
            g.drawRect(i,i,28 - i - i,28 - i - i);
         }
      
         frame.setIconImage(image);
      }
   
   }

