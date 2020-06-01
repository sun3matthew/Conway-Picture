
public class PixelAnimal extends Pixel
{
  boolean foundHome;
  public PixelAnimal(int rIn, int gIn, int bIn)
  {
    super(rIn, gIn, bIn);
    foundHome = false;
  }
  public PixelAnimal()
  {
    super();
  }
  public PixelAnimal(Pixel in)
  {
    this(in.r, in.g, in.b);
  }
  public PixelAnimal(Color colorIn)
  {
    super(colorIn.getRed(), colorIn.getGreen(), colorIn.getBlue());
  }
}
