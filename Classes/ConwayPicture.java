import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.Color;

/*

Conway's Game of Life that "draws" an image

java ConwayPicture filename

The images (in .jpg format) are added into a folder in the Input_Images,
the two images should be named One.jpg and Two.jpg, the "Two" image acts
as the world and the "One" image acts as the sample population that will
be selected to populate the world.

Random pixels from the the "One" image are selected and put into the world.
For the first 90 frames, the cells will not draw on the final image, each
new cell will slightly mutate towards the current world tile color it is
on when it is "born". After the first 90 frames, the cells will draw to
the final image.

Toggles q = draw || w = newGen || e = seeFinal || a = bg || s = cell

Exporting space = export

By: Matthew Sun
Since: May 31 2020
*/

/*
Main class to setup GUI
*/
public class ConwayPicture
{
 JFrame frame;
 AiProgram canvas;
 public static void main (String[] args)
 {
   String folderName = "Test";
   if(args.length > 0)
     folderName = args[0];
  ConwayPicture kt = new ConwayPicture();
  kt.Run(folderName);
 } // end main

 public void Run(String name) {
  frame = new JFrame("ConwayPicture || q = draw || w = newGen || e = seeFinal || a = bg || s = cell || space = export");
  frame.setSize(1050, 1050);
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  frame.setResizable(true);
  canvas = new AiProgram(name);
  frame.getContentPane().add(canvas);

  frame.setVisible(true);
 }
}

/*
Class the runs the program
*/
class AiProgram extends JPanel implements KeyListener{

Pixel[][] currentImage;
Pixel[][] currentAlive;
Pixel[][] colorImage;
Pixel[][] newCurrImage;
Pixel[][] finalImg;
Pixel[][] exportImage;
Boolean[][] doneOnes;
private Timer balltimer;
private int counter;
boolean toggleDraw;
boolean toggleCells;
boolean toggleBg;
boolean toggleExportImg;
int genCounter;
int genStartCounter;
String exportPath;
final int startWith = 16;
final int numberOfGens = 15;
final int survival = 100;
final int vision = 3;
final int stopAt = 1500;
/*
Constructor which reads/setup files and instance varibles.
*/
 public AiProgram(String folderName) {
   String dir = System.getProperty("user.dir");
   //System.out.println(dir.substring(0, dir.length()-7));
   String mainPath = dir.substring(0, dir.length()-7);//"/Users/matthewsun/Desktop/ConwayPicture/";
   String newFolder = mainPath+"Output_Images/"+folderName+"Output";
   File folder = new File(mainPath+"Input_Images/"+folderName);
   new File(newFolder).mkdirs();
   exportPath = newFolder+"/";
   File[] listOfFiles = folder.listFiles();
   int counter = 0;
   for(int i = 0; i < listOfFiles.length; i++)
   {
     if(listOfFiles[i].getName().indexOf("jpg") != -1)
     {
       String fileName = listOfFiles[i].getPath();
       Image picture = new Image(fileName);
       if(listOfFiles[i].getName().indexOf("One") != -1)
       {
         colorImage = picture.getData();
       }else
       {
         currentImage = picture.getData();
       }
     }
   }
   currentAlive = new Pixel[currentImage.length][currentImage[0].length];
   newCurrImage = new Pixel[currentImage.length][currentImage[0].length];
   finalImg = new Pixel[currentImage.length][currentImage[0].length];
   exportImage = new Pixel[currentImage.length][currentImage[0].length];
   toggleDraw = true;
   toggleCells = true;
   toggleBg = true;
   toggleExportImg = false;
   genCounter = 0;
   setFocusable(true);
   requestFocus();
   addKeyListener(this);
   startGen();
  BallMover ballmover = new BallMover();
  balltimer = new Timer(16, ballmover);//50
  balltimer.start();
 }

 /*
 This method populates the world with pixels from image One
 */
 public void startGen()
 {
   genStartCounter = 0;
   for(int row = 0; row < currentAlive.length; row++)
   {
     for(int col = 0; col < currentAlive[0].length; col++)
     {
         if(randomWithRange(0, 1) != 0)
          currentAlive[row][col] = new Pixel(colorImage[randomWithRange(0, colorImage.length-1)][randomWithRange(0, colorImage[0].length-1)]);
     }
   }
 }

 /*
 Helper method
 */
  public int randomWithRange(int min, int max)
  {
     int range = (max - min) + 1;
     return (int)(Math.random() * range) + min;
  }

  /*
  The "Update Function"
  */
 class BallMover implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    counter++;
    genStartCounter++;
    System.out.println("Generation: " + counter);
    for(int row = 0; row < currentAlive.length; row++)
    {
      for(int col = 0; col < currentAlive[0].length; col++)
      {
        if(genStartCounter >= 90 && currentAlive[row][col] != null && currentAlive[row][col].comparePixel(currentImage[row][col]) < 60)
        {
          finalImg[row][col] = currentAlive[row][col];
          exportImage[row][col] = currentAlive[row][col];
        }
        int numNeigh = 0;

        for(int i = -1; i <= 1; i++)
          for(int j = -1; j <= 1; j++)
            if(row+i >= 0 && row+i < currentAlive.length && col+j >= 0 && col+j < currentAlive[0].length && currentAlive[row+i][col+j] != null)
              numNeigh++;
        if(currentAlive[row][col] != null)
          numNeigh--;
        if(currentAlive[row][col] != null && (numNeigh < 2 || numNeigh > 3))
        {
          newCurrImage[row][col] = null;
        }else if(currentAlive[row][col] == null && numNeigh == 3)
        {
          Pixel[] parents = new Pixel[3];
          int parrCounter = 0;
          for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
              if(row+i >= 0 && row+i < currentAlive.length && col+j >= 0 && col+j < currentAlive[0].length && currentAlive[row+i][col+j] != null)
              {
                parents[parrCounter] = currentAlive[row+i][col+j];
                if(parrCounter != 2)
                  parrCounter++;
              }
          newCurrImage[row][col] = geiWoBaby(parents[0], parents[1], parents[2], currentImage[row][col], 9);
        }else
        {
          newCurrImage[row][col] = currentAlive[row][col];
        }
      }
    }
    for(int row = 0; row < currentAlive.length; row++)
    {
      for(int col = 0; col < currentAlive[0].length; col++)
      {
        currentAlive[row][col] = null;
        currentAlive[row][col] = newCurrImage[row][col];
        newCurrImage[row][col] = null;
      }
    }
    for(int row = 0; row < currentAlive.length; row++)
    {
      for(int col = 0; col < currentAlive[0].length; col++)
      {
        if(finalImg[row][col] != null && finalImg[row][col].grayscale() > 30)
          finalImg[row][col] = new Pixel((int)(finalImg[row][col].r*0.9), (int)(finalImg[row][col].g*0.9), (int)(finalImg[row][col].b*0.9));
      }
    }
    repaint();
    }
  }

  /*
  Counts the empty pixels
  */
  public int count()
  {
    int counter = 0;
    for(int row = 0; row < currentAlive.length; row++)
      for(int col = 0; col < currentAlive[0].length; col++)
        if(currentAlive[row][col] == null)
          counter++;
    return counter;
  }

  /*
  Returns a slightly mutated child
  */
   public Pixel geiWoBaby(Pixel parent1, Pixel parent2, Pixel parent3, Pixel imgColor, int mutationAmount)
   {
     int avgR = (parent1.r + parent2.r + parent3.r)/3;
     int avgG = (parent1.g + parent2.g + parent3.g)/3;
     int avgB = (parent1.b + parent2.b + parent3.b)/3;
     avgR += (imgColor.r - avgR)/randomWithRange(mutationAmount*5,mutationAmount*6);
     avgG += (imgColor.g - avgG)/randomWithRange(mutationAmount*5,mutationAmount*6);
     avgB += (imgColor.b - avgB)/randomWithRange(mutationAmount*5,mutationAmount*6);//+randomWithRange(-1*mutationAmount/2,mutationAmount/2)
     return new Pixel(avgR, avgG, avgB);
   }

   /*
   Draws the pixels on screen
   */
   public void paintComponent(Graphics g) {
     if(toggleExportImg)
     {
       super.paintComponent(g);
       setBackground(Color.BLACK);
       for(int row = 0; row < currentAlive.length; row++)
         for(int col = 0; col < currentAlive[0].length; col++)
         {
           g.setColor(Color.BLACK);
           if(currentAlive[row][col] != null)
           {
             g.setColor(currentAlive[row][col].getColor());
           }else if(exportImage[row][col] != null)
           {
             g.setColor(exportImage[row][col].getColor());
           }
           g.drawLine(col, row, col, row);
         }
     }else if(toggleDraw)
     {
       super.paintComponent(g);
       setBackground(Color.BLACK);
       for(int row = 0; row < currentAlive.length; row++)
         for(int col = 0; col < currentAlive[0].length; col++)
           if(currentAlive[row][col] != null || finalImg[row][col] != null)
           {
             g.setColor(Color.BLACK);
             if(currentAlive[row][col] != null && toggleCells)
             {
               g.setColor(currentAlive[row][col].getColor());
             }else if(toggleBg && finalImg[row][col] != null)
             {
               g.setColor(finalImg[row][col].getColor());
             }
             g.drawLine(col, row, col, row);
           }
     }
   } // end paintComponent`

   public Pixel[][] cloneArray(Pixel[][] cloneThis)
   {
     Pixel[][] newArray = new Pixel[cloneThis.length][cloneThis[0].length];
     for (int row = 0; row < newArray.length; row++)
      for (int col = 0; col < newArray[0].length; col++)
       newArray[row][col] = cloneThis[row][col];
     return newArray;
   }
   
    public void keyReleased(KeyEvent e) {}
   public void keyTyped(KeyEvent e) {
     if(e.getKeyChar() == ' ')
     {
       for(int row = 0; row < currentAlive.length; row++)
         for(int col = 0; col < currentAlive[0].length; col++)
         {
           if(currentAlive[row][col] != null)
             exportImage[row][col] = currentAlive[row][col];
           if(exportImage[row][col] == null)
             exportImage[row][col] = new Pixel();
         }
       Image newPicture = new Image(exportImage);
       newPicture.exportImage(exportPath+"OutputImg");
       System.exit(0);
     }
     if(e.getKeyChar() == 'q')
     {
       toggleDraw = !toggleDraw;
     }else if(e.getKeyChar() == 'w')
     {
       startGen();
     }else if(e.getKeyChar() == 'a')
     {
       toggleBg = !toggleBg;
     }else if(e.getKeyChar() == 's')
     {
       toggleCells = !toggleCells;
     }
     else if(e.getKeyChar() == 'e')
     {
       toggleExportImg = !toggleExportImg;
     }
   }
     public void keyPressed(KeyEvent e) {}

}
