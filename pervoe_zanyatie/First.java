package pervoe_zanyatie;

/*
public class First {
    public static void
    main(String[] args) {
    System.out.print("Hello World!");
    }
}
*/


/*
public class First {
    public static void
    main(String[] args) {
    System.out.print("Hello " + args[0]);
    }
}
*/


/*
public class First {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Hello World!");
        } else if (args.length == 1) {
            System.out.println("Hello " + args[0]);
        } else if (args.length > 1) {
            System.out.println("Hello " + args[0] + " " + args[1]);
        }
    }
}
*/


/*
import java.io.*;

public class First {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                InputStreamReader r = new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader(r);
                String a = br.readLine();
                System.out.println("Hello " + a + "!");
            } else if (args.length == 1) {
                System.out.println("Hello " + args[0] + "!");
            } else {
                System.out.println("Hello " + args[0] + " " + args[1] + "!");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
*/


/*
import java.io.*;
import java.util.Arrays;

public class First {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                File f1 = new File("name.txt");
                if (f1.exists() && f1.isFile() && f1.canRead()) {
                    FileReader fileReader = new FileReader(f1);
                    BufferedReader f2 = new BufferedReader(fileReader);

                    char[] buf = new char[100];
                    int c;
                    StringBuilder name = new StringBuilder();

                    while ((c = f2.read(buf)) > 0) {
                        if (c < 100) {
                            buf = Arrays.copyOf(buf, c);
                        }
                        name.append(new String(buf, 0, c));
                    }
                    f2.close();
                    
                    System.out.println("Hello " + name.toString() + "!"); 
                } else {
                    System.out.println("Пустое множество или файл недоступен");
                }
            } else if (args.length == 1) {
                System.out.println("Hello " + args[0] + "!");
            } else {
                System.out.println("Hello " + args[0] + " " + args[1] + "!");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
*/


import java.io.*;
import java.util.Arrays;

public class First {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                File f1 = new File("pervoe_zanyatie/name.txt"); // Указываем имя файла

                if (!f1.exists()) {
                    f1.createNewFile();
                }

                if (f1.canWrite()) {
                    FileWriter fw = new FileWriter(f1, true);
                    fw.write("Ksusha");
                    fw.close();
                }

                if (f1.exists() && f1.isFile() && f1.canRead()) {
                    FileReader fileReader = new FileReader(f1);
                    BufferedReader f2 = new BufferedReader(fileReader);

                    char[] buf = new char[100];
                    int c;
                    StringBuilder name = new StringBuilder();

                    while ((c = f2.read(buf)) > 0) {
                        if (c < 100) {
                            buf = Arrays.copyOf(buf, c);
                        }
                        name.append(new String(buf, 0, c));
                    }
                    f2.close();

                    System.out.println("Hello " + name.toString() + "!");
                } else {
                    System.out.println("Пустое множество или файл недоступен");
                }
            } else if (args.length == 1) {
                System.out.println("Hello " + args[0] + "!");
            } else {
                System.out.println("Hello " + args[0] + " " + args[1] + "!");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}







