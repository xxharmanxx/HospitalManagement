import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String date;
    private String startTime;
    private String endTime;
    private String treatmentType;

    public Appointment(String date, String startTime, String endTime, String treatmentType) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.treatmentType = treatmentType;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public boolean isOverlap(LocalDate startDate, LocalDate endDate, String startTime, String endTime) {
        // Convert input strings to LocalTime for easier comparison
        LocalTime inputStartTime = LocalTime.parse(startTime);
        LocalTime inputEndTime = LocalTime.parse(endTime);

        // Convert appointment start and end times to LocalTime
        LocalTime appointmentStartTime = LocalTime.parse(this.startTime);
        LocalTime appointmentEndTime = LocalTime.parse(this.endTime);

        // Check if dates overlap
        boolean dateOverlap = !(endDate.isBefore(LocalDate.parse(this.date)) || startDate.isAfter(LocalDate.parse(this.date)));

        // Check if times overlap
        boolean timeOverlap = !(inputEndTime.isBefore(appointmentStartTime) || inputStartTime.isAfter(appointmentEndTime));

        // Return true if both date and time overlap
        return dateOverlap && timeOverlap;
    }

    @Override
    public String toString() {
        return "Date: " + date + ", Time: " + startTime + " - " + endTime + ", Treatment Type: " + treatmentType;
    }
}

class RecurringAppointment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String treatmentType;

    public RecurringAppointment(String dayOfWeek, String startTime, String endTime, String treatmentType) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.treatmentType = treatmentType;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    @Override
    public String toString() {
        return "Recurring on " + dayOfWeek + " at " + startTime + " - " + endTime + ", Treatment Type: " + treatmentType;
    }
}

class HealthProfessional implements Serializable {
    private static final long serialVersionUID = 1L;
    public void setName(String name) {
        this.name = name;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }
    private String name;
    private String profession;
    private String workLocation;
    private List<Appointment> appointments;
    private List<RecurringAppointment> recurringAppointments;

    public HealthProfessional(String name, String profession, String workLocation) {
        this.name = name;
        this.profession = profession;
        this.workLocation = workLocation;
        this.appointments = new ArrayList<>();
        this.recurringAppointments = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getProfession() {
        return profession;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<RecurringAppointment> getRecurringAppointments() {
        return recurringAppointments;
    }

    public void scheduleAppointment(String date, String startTime, String endTime, String treatmentType) {
        Appointment appointment = new Appointment(date, startTime, endTime, treatmentType);
        appointments.add(appointment);
    }

    public void addRecurringAppointment(String dayOfWeek, String startTime, String endTime, String treatmentType) {
        RecurringAppointment recurringAppointment = new RecurringAppointment(dayOfWeek, startTime, endTime, treatmentType);
        recurringAppointments.add(recurringAppointment);
    }

    public void scheduleRecurringAppointments(LocalDate startDate, LocalDate endDate) {
        for (RecurringAppointment recurringAppointment : recurringAppointments) {
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                if (currentDate.getDayOfWeek().name().equalsIgnoreCase(recurringAppointment.getDayOfWeek())) {
                    String date = currentDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                    scheduleAppointment(date, recurringAppointment.getStartTime(), recurringAppointment.getEndTime(), recurringAppointment.getTreatmentType());
                }
                currentDate = currentDate.plusWeeks(1);
            }
        }
    }
}

class HealthProfessionalDataStore {
    private List<HealthProfessional> healthProfessionals;

    public HealthProfessionalDataStore() {
        this.healthProfessionals = new ArrayList<>();
    }

    public List<HealthProfessional> getHealthProfessionals() {
        return healthProfessionals;
    }

    public void addHealthProfessional(HealthProfessional professional) {
        healthProfessionals.add(professional);
    }

    public void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(healthProfessionals);
            System.out.println("Data saved to file successfully.\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving data to file.\n");
        }
    }

    public void loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            healthProfessionals = (List<HealthProfessional>) ois.readObject();
            System.out.println("Data loaded from file successfully.\n");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error loading data from file.\n");
        }
    }
}

public class HealthAuthorityAppointmentScheduler {
    private HealthProfessionalDataStore dataStore;

    public HealthAuthorityAppointmentScheduler(HealthProfessionalDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void displayDiaryEntries() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the health professional: ");
        String name = scanner.nextLine();

        HealthProfessional professional = findHealthProfessionalByName(name);

        if (professional != null) {
            System.out.println("Diary entries for " + professional.getName() + ":");
            System.out.println("Appointments:");
            professional.getAppointments().forEach(appointment -> {
                System.out.println("Date: " + appointment.getDate() +
                        ", Time: " + appointment.getStartTime() + " - " + appointment.getEndTime() +
                        ", Treatment Type: " + appointment.getTreatmentType());
            });

            System.out.println("Recurring Appointments:");
            professional.getRecurringAppointments().forEach(recurringAppointment -> {
                System.out.println("Recurring on " + recurringAppointment.getDayOfWeek() +
                        " at " + recurringAppointment.getStartTime() + " - " + recurringAppointment.getEndTime() +
                        ", Treatment Type: " + recurringAppointment.getTreatmentType());
            });
        } else {
            System.out.println("Health professional not found.\n");
        }

        scanner.close();
    }

    private HealthProfessional findHealthProfessionalByName(String name) {
        return dataStore.getHealthProfessionals().stream()
                .filter(professional -> professional.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void addHealthProfessional() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the new health professional: ");
        String name = scanner.nextLine();
        System.out.print("Enter the profession of the new health professional: ");
        String profession = scanner.nextLine();
        System.out.print("Enter the work location of the new health professional: ");
        String workLocation = scanner.nextLine();

        HealthProfessional newProfessional = new HealthProfessional(name, profession, workLocation);
        dataStore.addHealthProfessional(newProfessional);

        System.out.println("Health professional added successfully.\n");

        scanner.close();
    }

    public void deleteHealthProfessional() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the health professional to delete: ");
        String name = scanner.nextLine();

        HealthProfessional professionalToDelete = findHealthProfessionalByName(name);

        if (professionalToDelete != null) {
            dataStore.getHealthProfessionals().remove(professionalToDelete);
            System.out.println("Health professional deleted successfully.\n");
        } else {
            System.out.println("Health professional not found.\n");
        }

        scanner.close();
    }

    public void editHealthProfessional() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the health professional to edit: ");
        String name = scanner.nextLine();

        HealthProfessional professionalToEdit = findHealthProfessionalByName(name);

        if (professionalToEdit != null) {
            System.out.println("Enter new information:");

            System.out.print("Enter the new name: ");
            String newName = scanner.nextLine();
            System.out.print("Enter the new profession: ");
            String newProfession = scanner.nextLine();
            System.out.print("Enter the new work location: ");
            String newWorkLocation = scanner.nextLine();

            // Use the setter methods to update the fields
            professionalToEdit.setName(newName);
            professionalToEdit.setProfession(newProfession);
            professionalToEdit.setWorkLocation(newWorkLocation);

            System.out.println("Health professional edited successfully.\n");
        } else {
            System.out.println("Health professional not found.\n");
        }

        scanner.close();
    }



    public void saveDataToFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the filename to save data: ");
        String filename = scanner.nextLine();
        dataStore.saveToFile(filename);
    }

    public void loadDataFromFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the filename to load data: ");
        String filename = scanner.nextLine();
        dataStore.loadFromFile(filename);
    }

    public void schedulePatientAppointment() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the health professional: ");
        String name = scanner.nextLine();

        HealthProfessional professional = findHealthProfessionalByName(name);

        if (professional != null) {
            System.out.print("Enter the start date (MM/DD/YYYY) to search from: ");
            String startDateStr = scanner.nextLine();
            LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"));

            System.out.print("Enter the end date (MM/DD/YYYY) to search up to: ");
            String endDateStr = scanner.nextLine();
            LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"));

            System.out.print("Enter the start time (HH:mm) for the appointment: ");
            String appointmentStartTime = scanner.nextLine();

            System.out.print("Enter the end time (HH:mm) for the appointment: ");
            String appointmentEndTime = scanner.nextLine();

            System.out.print("Enter the treatment type for the appointment: ");
            String treatmentType = scanner.nextLine();

            if (isSlotAvailable(professional, startDate, endDate, appointmentStartTime, appointmentEndTime)) {
                professional.scheduleAppointment(startDateStr, appointmentStartTime, appointmentEndTime, treatmentType);
                System.out.println("Appointment scheduled successfully.\n");
            } else {
                System.out.println("No available slots found within the specified range.\n");
            }
        } else {
            System.out.println("Health professional not found.\n");
        }

        scanner.close();
    }

    private boolean isSlotAvailable(HealthProfessional professional, LocalDate startDate, LocalDate endDate, String startTime, String endTime) {
        List<Appointment> appointments = professional.getAppointments();
        for (Appointment appointment : appointments) {
            if (appointment.isOverlap(startDate, endDate, startTime, endTime)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        HealthProfessionalDataStore dataStore = new HealthProfessionalDataStore();
        HealthAuthorityAppointmentScheduler scheduler = new HealthAuthorityAppointmentScheduler(dataStore);

        // Example usage (you can customize this based on your needs)
        scheduler.addHealthProfessional();
        scheduler.displayDiaryEntries();
        scheduler.schedulePatientAppointment();
        scheduler.displayDiaryEntries();
        scheduler.saveDataToFile();
        scheduler.loadDataFromFile();
    }
}
