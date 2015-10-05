package net.sqf.view.utils.images;
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s): Alexandre Iline.
 *
 * The Original Software is the Jemmy library.
 * The Initial Developer of the Original Software is Alexandre Iline.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 *
 *
 * $Id$ $Revision$ $Date$
 *
 */


import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;



/**
 * Allows to load PNG graphical file.
 * @author Alexandre Iline
 */
public class PNGDecoder extends Object {

    InputStream in;

    /**
     * Constructs a PNGDecoder object.
     * @param in input stream to read PNG image from.
     */    
    public PNGDecoder(InputStream in) {
        this.in = in;
    }

    byte read() throws IOException {
        byte b = (byte)in.read();
        return(b);
    }

    int readInt() throws IOException {
        byte b[] = read(4);
        return(((b[0]&0xff)<<24) +
               ((b[1]&0xff)<<16) +
               ((b[2]&0xff)<<8) +
               ((b[3]&0xff)));
    }

    byte[] read(int count) throws IOException {
        byte[] result = new byte[count];
        for(int i = 0; i < count; i++) {
            result[i] = read();
        }
        return(result);
    }

    boolean compare(byte[] b1, byte[] b2) {
        if(b1.length != b2.length) {
            return(false);
        }
        for(int i = 0; i < b1.length; i++) {
            if(b1[i] != b2[i]) {
                return(false);
            }
        }
        return(true);
    }

    void checkEquality(byte[] b1, byte[] b2) {
        if(!compare(b1, b2)) {
            throw(new RuntimeException("Format error"));
        }
    }

    /**
     * Decodes image from an input stream passed into constructor.
     * @return a BufferedImage object
     * @throws IOException
     */
    public BufferedImage decode() throws IOException {

        byte[] id = read(12);
        checkEquality(id, new byte[] {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13});

        byte[] ihdr = read(4);
        checkEquality(ihdr, "IHDR".getBytes());

        int width = readInt();
        int height = readInt();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        byte[] head = read(5);
        int mode;
        if(compare(head, new byte[]{1, 0, 0, 0, 0})) {
            mode = PNGEncoder.BW_MODE;
        } else if(compare(head, new byte[]{8, 0, 0, 0, 0})) {
            mode = PNGEncoder.GREYSCALE_MODE;
        } else if(compare(head, new byte[]{8, 2, 0, 0, 0})) {
            mode = PNGEncoder.COLOR_MODE;
        } else {
            throw(new RuntimeException("Format error"));
        }

        readInt();//!!crc

        int size = readInt();

        byte[] idat = read(4);
        checkEquality(idat, "IDAT".getBytes());

        byte[] data = read(size);


        Inflater inflater = new Inflater();
        inflater.setInput(data, 0, size);

        int color;

        try {
            switch (mode) {
            case PNGEncoder.BW_MODE: 
                {
                    int bytes = (int)(width / 8);
                    if((width % 8) != 0) {
                        bytes++;
                    }
                    byte colorset;
                    byte[] row = new byte[bytes];
                    for (int y = 0; y < height; y++) {
                        inflater.inflate(new byte[1]);
                        inflater.inflate(row);
                        for (int x = 0; x < bytes; x++) {
                            colorset = row[x];
                            for (int sh = 0; sh < 8; sh++) {
                                if(x * 8 + sh >= width) {
                                    break;
                                }
                                if((colorset & 0x80) == 0x80) {
                                    result.setRGB(x * 8 + sh, y, Color.white.getRGB());
                                } else {
                                    result.setRGB(x * 8 + sh, y, Color.black.getRGB());
                                }
                                colorset <<= 1;
                            }
                        }
                    }
                }
                break;
            case PNGEncoder.GREYSCALE_MODE: 
                {
                    byte[] row = new byte[width];
                    for (int y = 0; y < height; y++) {
                        inflater.inflate(new byte[1]);
                        inflater.inflate(row);
                        for (int x = 0; x < width; x++) {
                            color = row[x];
                            result.setRGB(x, y, (color << 16) + (color << 8) + color);
                        }
                    }
                }
                break;
            case PNGEncoder.COLOR_MODE:
                {
                    byte[] row = new byte[width * 3];
                    for (int y = 0; y < height; y++) {
                        inflater.inflate(new byte[1]);
                        inflater.inflate(row);
                        for (int x = 0; x < width; x++) {
                            result.setRGB(x, y, 
                                          ((row[x * 3 + 0]&0xff) << 16) +
                                          ((row[x * 3 + 1]&0xff) << 8) +
                                          ((row[x * 3 + 2]&0xff)));
                        }
                    }
                }
            }
        } catch(DataFormatException e) {
            throw(new RuntimeException("ZIP error"+e));
        }

        readInt();//!!crc
        readInt();//0

        byte[] iend = read(4);
        checkEquality(iend, "IEND".getBytes());

        readInt();//!!crc
        in.close();

        return(result);
    }

    /**
     * Decodes image from file.
     * @param fileName a file to read image from
     * @return a BufferedImage instance.
     */
    public static BufferedImage decode(String fileName) {
        try {
            return(new PNGDecoder(new FileInputStream(fileName)).decode());
        } catch(IOException e) {
            throw(new RuntimeException("IOException during image reading"+ e));
        }
    }

}

class PNGEncoder extends Object {

  /** black and white image mode. */    
  public static final byte BW_MODE = 0;
  /** grey scale image mode. */    
  public static final byte GREYSCALE_MODE = 1;
  /** full color image mode. */    
  public static final byte COLOR_MODE = 2;
  
  OutputStream out;
  CRC32 crc;
  byte mode;

  /** public constructor of PNGEncoder class with greyscale mode by default.
   * @param out output stream for PNG image format to write into
   */    
  public PNGEncoder(OutputStream out) {
      this(out, GREYSCALE_MODE);
  }

  /** public constructor of PNGEncoder class.
   * @param out output stream for PNG image format to write into
   * @param mode BW_MODE, GREYSCALE_MODE or COLOR_MODE
   */    
  public PNGEncoder(OutputStream out, byte mode) {
      crc=new CRC32();
      this.out = out;
      if (mode<0 || mode>2)
          throw new IllegalArgumentException("Unknown color mode");
      this.mode = mode;
  }

  void write(int i) throws IOException {
      byte b[]={(byte)((i>>24)&0xff),(byte)((i>>16)&0xff),(byte)((i>>8)&0xff),(byte)(i&0xff)};
      write(b);
  }

  void write(byte b[]) throws IOException {
      out.write(b);
      crc.update(b);
  }
  
  /** main encoding method (stays blocked till encoding is finished).
   * @param image BufferedImage to encode
   * @throws IOException IOException
   */    
  public void encode(BufferedImage image) throws IOException {
      int width = image.getWidth(null);
      int height = image.getHeight(null);
      final byte id[] = {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13};
      write(id);
      crc.reset();
      write("IHDR".getBytes());
      write(width);
      write(height);
      byte head[]=null;
      switch (mode) {
          case BW_MODE: head=new byte[]{1, 0, 0, 0, 0}; break;
          case GREYSCALE_MODE: head=new byte[]{8, 0, 0, 0, 0}; break;
          case COLOR_MODE: head=new byte[]{8, 2, 0, 0, 0}; break;
      }                 
      write(head);
      write((int) crc.getValue());
      ByteArrayOutputStream compressed = new ByteArrayOutputStream(65536);
      BufferedOutputStream bos = new BufferedOutputStream( new DeflaterOutputStream(compressed, new Deflater(9)));
      int pixel;
      int color;
      int colorset;
      switch (mode) {
          case BW_MODE: 
              int rest=width%8;
              int bytes=width/8;
              for (int y=0;y<height;y++) {
                  bos.write(0);
                  for (int x=0;x<bytes;x++) {
                      colorset=0;
                      for (int sh=0; sh<8; sh++) {
                          pixel=image.getRGB(x*8+sh,y);
                          color=((pixel >> 16) & 0xff);
                          color+=((pixel >> 8) & 0xff);
                          color+=(pixel & 0xff);
                          colorset<<=1;
                          if (color>=3*128)
                              colorset|=1;
                      }
                      bos.write((byte)colorset);
                  }
                  if (rest>0) {
                      colorset=0;
                      for (int sh=0; sh<width%8; sh++) {
                          pixel=image.getRGB(bytes*8+sh,y);
                          color=((pixel >> 16) & 0xff);
                          color+=((pixel >> 8) & 0xff);
                          color+=(pixel & 0xff);
                          colorset<<=1;
                          if (color>=3*128)
                              colorset|=1;
                      }
                      colorset<<=8-rest;
                      bos.write((byte)colorset);
                  }
              }
              break;
          case GREYSCALE_MODE: 
              for (int y=0;y<height;y++) {
                  bos.write(0);
                  for (int x=0;x<width;x++) {
                      pixel=image.getRGB(x,y);
                      color=((pixel >> 16) & 0xff);
                      color+=((pixel >> 8) & 0xff);
                      color+=(pixel & 0xff);
                      bos.write((byte)(color/3));
                  }
              }
              break;
           case COLOR_MODE:
              for (int y=0;y<height;y++) {
                  bos.write(0);
                  for (int x=0;x<width;x++) {
                      pixel=image.getRGB(x,y);
                      bos.write((byte)((pixel >> 16) & 0xff));
                      bos.write((byte)((pixel >> 8) & 0xff));
                      bos.write((byte)(pixel & 0xff));
                  }
              }
              break;
      }
      bos.close();
      write(compressed.size());
      crc.reset();
      write("IDAT".getBytes());
      write(compressed.toByteArray());
      write((int) crc.getValue()); 
      write(0);
      crc.reset();
      write("IEND".getBytes());
      write((int) crc.getValue()); 
      out.close();
  }

  /** Static method performing screen capture into PNG image format file with given fileName.
   * @param rect Rectangle of screen to be captured
   * @param fileName file name for screen capture PNG image file 
 * @throws IOException 
 * @throws AWTException */    
  public static void captureScreen(Rectangle rect, String fileName) throws AWTException, IOException {
      captureScreen(rect, fileName, GREYSCALE_MODE);
  }

  /** Static method performing screen capture into PNG image format file with given fileName.
   * @param rect Rectangle of screen to be captured
   * @param mode image color mode
   * @param fileName file name for screen capture PNG image file 
 * @throws AWTException 
 * @throws IOException */    
  public static void captureScreen(Rectangle rect, String fileName, byte mode) throws AWTException, IOException {
          BufferedImage capture=new Robot().createScreenCapture(rect);
          BufferedOutputStream file=new BufferedOutputStream(new FileOutputStream(fileName));
          PNGEncoder encoder=new PNGEncoder(file, mode);
          encoder.encode(capture);
  }

   /** Static method performing one component screen capture into PNG image format file with given fileName.
    * @param comp Component to be captured
    * @param fileName String image target filename 
 * @throws IOException 
 * @throws AWTException */    
  public static void captureScreen(Component comp, String fileName) throws AWTException, IOException {
      captureScreen(comp, fileName, GREYSCALE_MODE);
  }
  
  /** Static method performing one component screen capture into PNG image format file with given fileName.
   * @param comp Component to be captured
   * @param fileName String image target filename
   * @param mode image color mode 
 * @throws IOException 
 * @throws AWTException */    
  public static void captureScreen(Component comp, String fileName, byte mode) throws AWTException, IOException {
captureScreen(new Rectangle(comp.getLocationOnScreen(),
          comp.getSize()),
        fileName, mode);
  }

  
  /** Static method performing whole screen capture into PNG image format file with given fileName.
   * @param fileName String image target filename 
 * @throws IOException 
 * @throws AWTException 
 * @throws HeadlessException */    
  public static void captureScreen(String fileName) throws HeadlessException, AWTException, IOException {
      captureScreen(fileName, GREYSCALE_MODE);
  }
  
  /** Static method performing whole screen capture into PNG image format file with given fileName.
   * @param fileName String image target filename
   * @param mode image color mode 
 * @throws IOException 
 * @throws AWTException 
 * @throws HeadlessException */    
  public static void captureScreen(String fileName, byte mode) throws HeadlessException, AWTException, IOException {
captureScreen(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()), fileName, mode);
  }
}