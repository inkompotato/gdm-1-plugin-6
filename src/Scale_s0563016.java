import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class Scale_s0563016 implements PlugInFilter {

    private ImagePlus imp;

    public static void main(String args[]) {
        //ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
        //ij.exitWhenQuitting(true);

        IJ.open("C:\\Users\\frajs\\Creative Cloud Files\\Eclipse\\workspace-gdm1\\gdm-1-plugin-6\\component.jpg");


        Scale_s0563016 sd = new Scale_s0563016();
        //sd.imp = IJ.getImage();
        //ImageProcessor B_ip = sd.imp.getProcessor();
        sd.run(WindowManager.getCurrentImage().getProcessor());
    }

    public int setup(String arg, ImagePlus imp) {
        //this.imp = imp;
        if (arg.equals("about")) {
            showAbout();
            return DONE;
        }
        return DOES_RGB + NO_CHANGES;
        // kann RGB-Bilder und veraendert das Original nicht
    }

    public void run(ImageProcessor ip) {

        String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear"};

        GenericDialog gd = new GenericDialog("scale");
        gd.addChoice("Methode", dropdownmenue, dropdownmenue[0]);
        gd.addNumericField("Hoehe:", 500, 0);
        gd.addNumericField("Breite:", 400, 0);

        gd.showDialog();

        int height_n = (int) gd.getNextNumber(); // _n fuer das neue skalierte Bild
        int width_n = (int) gd.getNextNumber();
        String choice = gd.getNextChoice();

        int width = ip.getWidth();  // Breite bestimmen
        int height = ip.getHeight(); // Hoehe bestimmen

        //height_n = height;
        //width_n  = width;

        ImagePlus neu = NewImage.createRGBImage("Skaliertes Bild",
                width_n, height_n, 1, NewImage.FILL_BLACK);

        ImageProcessor ip_n = neu.getProcessor();


        int[] pix = (int[]) ip.getPixels();
        int[] pix_n = (int[]) ip_n.getPixels();

        float scaleY = (float)height/(float)height_n;
        float scaleX = (float)width/(float)width_n;

        float scaleY1 = (float)height_n/(float)height;
        float scaleX1 = (float)width_n/(float)width;

        // Schleife ueber das neue Bildefrgrrthththghzhz
        for (int y_n = 0; y_n < height_n; y_n++) {
            for (int x_n = 0; x_n < width_n; x_n++) {

                switch (choice) {
                    case "Kopie": {

                        int y = y_n;
                        int x = x_n;

                        if (y < height && x < width) {
                            int pos_n = y_n * width_n + x_n;
                            int pos = y * width + x;

                            pix_n[pos_n] = pix[pos];
                        }
                        break;
                    }
                    case "Pixelwiederholung": {

                        int x = Math.round(x_n * scaleX);
                        int y = Math.round(y_n * scaleY);

                        if (y < height && x < width) {
                            int pos_n = y_n * width_n + x_n;
                            int pos = y * width + x;

                            pix_n[pos_n] = pix[pos];
                        }

                        break;
                    }
                    case "Bilinear":
                        int pos_n = y_n * width_n + x_n;

                        float x = x_n / scaleX1;
                        float y = y_n / scaleY1;

                        float xDistance = x - (int) x;
                        float yDistance = y - (int) y;

                        int[][] neighbors = new int[4][3];

                        //Get neighbors
                        for (int i = 0; i < 4; i++) {
                            int xCoord = (int) x;
                            int yCoord = (int) y;

                            switch (i) {
                                case 3:
                                    xCoord = (xCoord + 1 < width) ? xCoord + 1 : width - 1;
                                case 2:
                                    yCoord = (yCoord + 1 < height) ? yCoord + 1 : height - 1;
                                    break;
                                case 1:
                                    xCoord = (xCoord + 1 < width) ? xCoord + 1 : width - 1;
                            }

                            int pos = yCoord * width + xCoord;

                            int color = pix[pos];
                            neighbors[i][0] = (color & 0xff0000) >> 16;
                            neighbors[i][1] = (color & 0xff00) >> 8;
                            neighbors[i][2] = (color & 0xff);
                        }

                        //Calculate new color
                        int r_n = (int) (neighbors[0][0] * (1 - xDistance) * (1 - yDistance) + neighbors[1][0] * xDistance * (1 - yDistance) + neighbors[2][0] * (1 - xDistance) * yDistance + neighbors[3][0] * xDistance * yDistance);
                        int g_n = (int) (neighbors[0][1] * (1 - xDistance) * (1 - yDistance) + neighbors[1][1] * xDistance * (1 - yDistance) + neighbors[2][1] * (1 - xDistance) * yDistance + neighbors[3][1] * xDistance * yDistance);
                        int b_n = (int) (neighbors[0][2] * (1 - xDistance) * (1 - yDistance) + neighbors[1][2] * xDistance * (1 - yDistance) + neighbors[2][2] * (1 - xDistance) * yDistance + neighbors[3][2] * xDistance * yDistance);

                        pix_n[pos_n] = 0xff000000 | r_n << 16 | g_n << 8 | b_n;
                        break;
                }


            }
                }




        // neues Bild anzeigen
        neu.show();
        neu.updateAndDraw();
    }



    void showAbout() {
        IJ.showMessage("");
    }
}
