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

        float h = (float)height/(float)height_n;
        float v = (float)width/(float)width_n;

        // Schleife ueber das neue Bild
        for (int y_n = 0; y_n < height_n; y_n++) {
            for (int x_n = 0; x_n < width_n; x_n++) {

                if (choice.equals("Kopie")) {

                    int y = y_n;
                    int x = x_n;

                    if (y < height && x < width) {
                        int pos_n = y_n * width_n + x_n;
                        int pos = y * width + x;

                        pix_n[pos_n] = pix[pos];
                    }
                }
                else if (choice.equals("Pixelwiederholung")) {

                    int x = Math.round(x_n*v);
                    int y = Math.round(y_n*h);

                    if (y < height && x < width) {
                        int pos_n = y_n * width_n + x_n;
                        int pos = y * width + x;

                        pix_n[pos_n] = pix[pos];
                    }

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
