import java.io.*;
import java.util.Scanner;

interface InformationProvider {
    String getInfo();
}

interface Connectable {
    void connect();
    void disconnect();
}
abstract class ElectronicDevice implements InformationProvider, Serializable {
    private String brand;
    protected double price;

    public ElectronicDevice(String brand, double price) {
        this.brand = brand;
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
class Cable extends ElectronicDevice implements Connectable, Serializable {
    private int length;

    public Cable(String brand, double price, int length) {
        super(brand, price);
        this.length = length;
    }

    public Cable() {
        super("", 0); // Вызываем конструктор родительского класса с пустыми значениями
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public void connect() {
        System.out.println("Кабель подключен.");
    }

    @Override
    public void disconnect() {
        System.out.println("Кабель отключен.");
    }

    @Override
    public String getInfo() {
        return "Кабель [бренд=" + getBrand() + ", цена=" + getPrice() + ", длина=" + length + "]";
    }
}

class Capability extends ElectronicDevice implements Connectable, InformationProvider, Serializable {
    private String feature;

    public Capability(String brand, double price, String feature) {
        super(brand, price);
        this.feature = feature;
    }

    public Capability() {
        super("", 0); // Вызываем конструктор родительского класса с пустыми значениями
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    @Override
    public void connect() {
        System.out.println("Возможность подключена.");
    }

    @Override
    public void disconnect() {
        System.out.println("Возможность отключена.");
    }

    @Override
    public String getInfo() {
        return "Возможность [бренд=" + getBrand() + ", цена=" + getPrice() + ", особенность=" + feature + "]";
    }
}

class Case extends ElectronicDevice implements InformationProvider, Serializable {
    private String material;

    public Case(String brand, double price, String material) {
        super(brand, price);
        this.material = material;
    }

    public Case() {
        super("", 0); // Вызываем конструктор родительского класса с пустыми значениями
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    @Override
    public String getInfo() {
        return "Корпус [бренд=" + getBrand() + ", цена=" + getPrice() + ", материал=" + material + "]";
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Connectable[] connectableDevices = new Connectable[10];
        InformationProvider[] infoProviderDevices = new InformationProvider[10];
        int count = 0;
        boolean continueInput = true;

        while (continueInput && count < connectableDevices.length) {
            System.out.println("Введите данные для устройства " + (count + 1) + ":");
            System.out.print("Бренд: ");
            String brand = scanner.nextLine();
            System.out.print("Цена: ");
            double price = scanner.nextDouble();
            scanner.nextLine();

            System.out.println("Выберите тип устройства:");
            System.out.println("1. Кабель");
            System.out.println("2. Возможность");
            System.out.println("3. Корпус");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Введите длину кабеля: ");
                    int length = scanner.nextInt();
                    scanner.nextLine();
                    Cable cable = new Cable(brand, price, length);
                    infoProviderDevices[count] = cable;
                    break;
                case 2:
                    System.out.print("Введите особенность устройства: ");
                    String feature = scanner.nextLine();
                    Capability capability = new Capability(brand, price, feature);
                    infoProviderDevices[count] = capability;
                    break;
                case 3:
                    System.out.print("Введите материал корпуса: ");
                    String material = scanner.nextLine();
                    Case caseDevice = new Case(brand, price, material);
                    infoProviderDevices[count] = caseDevice;
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, выберите 1, 2 или 3.");
                    continue;
            }

            count++;

            System.out.print("Продолжить ввод данных? (y/n): ");
            String input = scanner.nextLine();
            if (!input.equalsIgnoreCase("y")) {
                continueInput = false;
            }
        }
        System.out.println();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("devices_info.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            // Сохранение информации о типе объекта вместе с самим объектом в файле
            for (InformationProvider device : infoProviderDevices) {
                if (device != null) {
                    objectOutputStream.writeObject(device.getClass().getName()); // Сохранение имени класса
                    objectOutputStream.writeObject(device); // Сохранение объекта
                }
            }
            objectOutputStream.close();

            FileInputStream fileInputStream = new FileInputStream("devices_info.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            System.out.println("Информация об устройствах из файла:");
            System.out.println();
            while (true) {
                try {
                    String className = (String) objectInputStream.readObject();
                    InformationProvider device = (InformationProvider) objectInputStream.readObject();
                    if (device != null) {
                        System.out.println("Тип устройства: " + className);
                        System.out.println(device.getInfo());
                        System.out.println();
                    }
                } catch (EOFException e) {
                    break;
                }
            }
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        scanner.close();
    }
}
