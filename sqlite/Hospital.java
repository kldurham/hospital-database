//package org.cpsc5133;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DatabaseMetaData;
import java.io.*;
import java.util.*;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Types;

public class Hospital {

    private String DATABASE_STRING = "jdbc:sqlite:C://temp//hospital.db";
    private String DATABASE_PATH = "C:/temp/";
    private String DATABASE_NAME = "hospital.db";


    public static void main(String[] args) throws Exception {
        // menu for hospital data activities
        int choice = 0;
        String queryString = "";
        Hospital app = new Hospital();
        //get started by choosing what to do
        Scanner userInputMenuInput;
        userInputMenuInput = new Scanner(System.in);
        do {
            System.out.println("Main Menu: Please select a code\n"
                    + "1 - Room Information\n"
                    + "2 - Patient Information\n"
                    + "3 - Diagnosis and Treatment Information\n"
                    + "4 - Employee Information\n"
                    + "5 - Load a Personnel File\n"
                    + "6 - Load a Treatment File\n"
                    + "7 - Delete and recreate the Database\n"
                    + "0 - Quit\n");
            System.out.print("Enter Category Code: ");
            choice = userInputMenuInput.nextInt();
            userInputMenuInput.nextLine(); //throw away the \n not consumed by nextInt()
            switch (choice) {
                case 1:
                    System.out.println("Enter Operation Code: ");
                    System.out.print("1: Occupied rooms (patient and date)\n"
                            + "2: Open Rooms\n"
                            + "3: All rooms\n");
                    try {
                        choice = userInputMenuInput.nextInt();
                        switch (choice) {
                            case 1:
                                System.out.println("1.1. List the rooms that are occupied, along with the associated patient names and the date the patient was admitted.");
                                //app.occupiedRooms();
                                queryString = "\tSELECT rooms.room_number,\n" +
                                        "\t       patients.patient_first_name,\n" +
                                        "\t       patients.patient_last_name,\n" +
                                        "\t       patient_rooms.start_timestamp,\n" +
                                        "\t       patient_rooms.end_timestamp\n" +
                                        "\tFROM (patient_rooms    patient_rooms\n" +
                                        "\t      INNER JOIN patients patients\n" +
                                        "\t\t ON (patient_rooms.patient_id = patients.patient_id))\n" +
                                        "\t     INNER JOIN rooms rooms\n" +
                                        "\t\tON (patient_rooms.room_id = rooms.room_id)\n" +
                                        "\t; "
                                ;
                                app.runQuery(queryString);
                                break;
                            case 2:
                                System.out.println("1.2. List the rooms that are currently unoccupied. ");
                                queryString = "\tSELECT rooms.room_number\n" +
                                        "\tFROM rooms rooms\n" +
                                        "\tLEFT OUTER JOIN patient_rooms ON patient_rooms.room_id = rooms.room_id\n" +
                                        "\tWHERE patient_rooms.patient_room_id IS NULL\n" +
                                        "\t;"
                                ;
                                app.runQuery(queryString);
                                break;
                            case 3:
                                System.out.println("1.3. List all rooms in the hospital along with patient names and admission dates for those that are occupied. ");
                                queryString = "\tSELECT rooms.room_number,\n" +
                                        "\t       patients.patient_first_name,\n" +
                                        "\t       patients.patient_last_name,\n" +
                                        "\t       patient_rooms.start_timestamp\n" +
                                        "\tFROM rooms rooms\n" +
                                        "\tLEFT JOIN patient_rooms    patient_rooms\n" +
                                        "\t     ON patient_rooms.room_id = rooms.room_id\n" +
                                        "\t     AND patient_rooms.start_timestamp IS NOT NULL\n" +
                                        "\t     AND patient_rooms.end_timestamp IS NULL -- currently occupied  \n" +
                                        "\tLEFT JOIN patients patients\n" +
                                        "\t     ON patients.patient_id = patient_rooms.patient_id\n" +
                                        "\torder by rooms.room_id ASC\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            default:
                                System.out.println("\t*** Please choose a valid value ***\n");
                                break;
                        }
                    } catch (Exception e) {
                        System.out.println("\t\"" + choice
                                + "\" not found\n");
                    }
                    break;
                case 2:
                    System.out.println("Enter Operation Code:\n"
                            + "1: All patient full info\n"
                            + "2: Admitted Patients\n"
                            + "3: Patients during Date Range\n"
                            + "4: Discharged Patients during Date Range\n"
                            + "5: Current outpatients\n"
                            + "6: Outpatients during Date Range\n"
                            + "7: Specific patient information\n"
                            + "8: Specific patient treatment history\n"
                            + "9: 30 day repeat patient\n"
                            + "10: All Patients admission history\n");
                    try {
                        choice = userInputMenuInput.nextInt();
                        switch (choice) {
                            case 1:
                                System.out.println("2.1. List all patients in the database, with full personal information. ");
                                queryString = "SELECT patients.patient_first_name,\n" +
                                        "\t       patients.patient_last_name,\n" +
                                        "\t       patients.emergency_contact_name,\n" +
                                        "\t       patients.emergency_contact_number,\n" +
                                        "\t       patients.insurance_policy_number,\n" +
                                        "\t       patients.insurance_policy_company,\n" +
                                        "\t       patients.primary_physician_last_name\n" +
                                        "\tFROM patients patients\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 2:
                                System.out.println("2.2. List all patients currently admitted to the hospital (i.e., those who are currently receiving inpatient\n" +
                                        "services). List only patient identification number and name. \n");
                                queryString = "SELECT patients.patient_id,\n" +
                                        "       patients.patient_first_name,\n" +
                                        "       patients.patient_last_name\n" +
                                        "FROM patient_rooms    patient_rooms\n" +
                                        "     INNER JOIN patients patients\n" +
                                        "        ON (patient_rooms.patient_id = patients.patient_id);";
                                app.runQuery(queryString);
                                break;
                            case 3:
                                System.out.println("2.3. List all patients who were receiving inpatient services within a given date range. List only patient\n" +
                                        "identification number and name.");
                                System.out.println("Please provide beginning start date (yyyy-mm-dd)");
                                userInputMenuInput.nextLine(); //throw away the \n not consumed by nextInt() from previous menu
                                String userStartDate = userInputMenuInput.nextLine();
                                System.out.println("Please provide end date (yyyy-mm-dd)");
                                String userEndDate = userInputMenuInput.nextLine();
                                queryString = "SELECT patients.patient_id,\n" +
                                        "\t       patients.patient_first_name,\n" +
                                        "\t       patients.patient_last_name\n" +
                                        "\tFROM patient_rooms    patient_rooms\n" +
                                        "\t     INNER JOIN patients patients\n" +
                                        "        ON (patient_rooms.patient_id = patients.patient_id)\n" +
                                        "\tAND patient_rooms.start_timestamp between '" + userStartDate + "' and '" + userEndDate + "'\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 4:
                                System.out.println("2.4. List all patients who were discharged in a given date range. List only patient identification number and\n" +
                                        "name. ");
                                System.out.println("Please provide beginning start date (yyyy-mm-dd)");
                                userInputMenuInput.nextLine(); //throw away the \n not consumed by nextInt() from previous menu
                                userStartDate = userInputMenuInput.nextLine();
                                System.out.println("Please provide end date (yyyy-mm-dd)");
                                userEndDate = userInputMenuInput.nextLine();
                                queryString = "SELECT patients.patient_id,\n" +
                                        "\t       patients.patient_first_name,\n" +
                                        "\t       patients.patient_last_name\n" +
                                        "\tFROM patient_rooms    patient_rooms\n" +
                                        "\t     INNER JOIN patients patients\n" +
                                        "        ON (patient_rooms.patient_id = patients.patient_id)\n" +
                                        "\tAND patient_rooms.end_timestamp between '" + userStartDate + "' and '" + userEndDate + "'\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 5:
                                System.out.println("2.5. List all patients who are currently receiving outpatient services. List only patient identification number\n" +
                                        "and name. \n");
                                queryString = "SELECT patients.patient_id,\n" +
                                        "\t       patients.patient_first_name,\n" +
                                        "\t       patients.patient_last_name\n" +
                                        "\tFROM ((treatment_components    treatment_components\n" +
                                        "\t       INNER JOIN services services\n" +
                                        "\t\t  ON (treatment_components.service_id = services.service_id))\n" +
                                        "\t      INNER JOIN\n" +
                                        "\t      patient_treatment_components patient_treatment_components\n" +
                                        "\t\t ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\ttreatment_components.treatment_component_id))\n" +
                                        "\t     INNER JOIN patients patients\n" +
                                        "\t\tON (patient_treatment_components.patient_id = patients.patient_id)\n" +
                                        "\tWHERE     (services.service_type = 'outpatient')\n" +
                                        "\t      AND (patient_treatment_components.treatment_end_timestamp IS NULL)\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 6:
                                System.out.println("2.6. List all patients who have received outpatient services within a given date range. List only patient\n" +
                                        "identification number and name. ");
                                System.out.println("Please provide beginning start date (yyyy-mm-dd)");
                                userInputMenuInput.nextLine(); //throw away the \n not consumed by nextInt() from previous menu
                                userStartDate = userInputMenuInput.nextLine();
                                System.out.println("Please provide end date (yyyy-mm-dd)");
                                userEndDate = userInputMenuInput.nextLine();

                                queryString = "SELECT patients.patient_id,\n" +
                                        "\t       patients.patient_first_name,\n" +
                                        "\t       patients.patient_last_name\n" +
                                        "\tFROM ((treatment_components    treatment_components\n" +
                                        "\t       INNER JOIN services services\n" +
                                        "\t\t  ON (treatment_components.service_id = services.service_id))\n" +
                                        "\t      INNER JOIN\n" +
                                        "\t      patient_treatment_components patient_treatment_components\n" +
                                        "\t\t ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\ttreatment_components.treatment_component_id))\n" +
                                        "\t     INNER JOIN patients patients\n" +
                                        "\t\tON (patient_treatment_components.patient_id = patients.patient_id)\n" +
                                        "\tWHERE     (services.service_type = 'outpatient')\n" +
                                        "\t      AND (patient_treatment_components.treatment_start_timestamp BETWEEN '" + userStartDate + "' AND '" + userEndDate + "'\n" +
                                        "\t            OR\n" +
                                        "\t           patient_treatment_components.treatment_end_timestamp BETWEEN '" + userStartDate + "' AND '" + userEndDate + "'\n" +
                                        "\t      )\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 7:
                                System.out.println("2.7. For a given patient (either patient identification number or name), list all admissions to the hospital\n" +
                                        "along with the diagnosis for each admission. ");
                                System.out.println("1: Give patient name\n"
                                        + "2: Give patient number");
                                choice = userInputMenuInput.nextInt();
                                switch (choice) {
                                    case 1:
                                        System.out.println("Patient Last Name?: ");
                                        userInputMenuInput.nextLine(); //throw away the \n not consumed by nextInt() from previous menu
                                        String patientName = userInputMenuInput.nextLine();
                                        queryString = "SELECT patients.patient_id,\n" +
                                                "\t       patients.patient_first_name,\n" +
                                                "\t       patients.patient_last_name,\n" +
                                                "\t       treatments.associated_diagnosis,\n" +
                                                "\t       patient_rooms.start_timestamp\n" +
                                                "\tFROM (((patient_treatment_components    patient_treatment_components\n" +
                                                "\t\tINNER JOIN treatment_components treatment_components\n" +
                                                "\t\t   ON (patient_treatment_components.treatment_component_id =\n" +
                                                "\t\t\t  treatment_components.treatment_component_id))\n" +
                                                "\t       INNER JOIN patients patients\n" +
                                                "\t\t  ON (patient_treatment_components.patient_id = patients.patient_id))\n" +
                                                "\t      INNER JOIN patient_rooms patient_rooms\n" +
                                                "\t\t ON (patient_rooms.patient_id = patients.patient_id))\n" +
                                                "\t     INNER JOIN treatments treatments\n" +
                                                "\t\tON (treatment_components.treatment_id = treatments.treatment_id)\n" +
                                                "\tWHERE   lower(patients.patient_last_name)      = lower('" + patientName + "')\n" +
                                                "\t;\n";
                                        break;
                                    case 2:
                                        System.out.println("Patient Identification Number?: ");
                                        userInputMenuInput.nextLine(); //throw away the \n not consumed by nextInt() from previous menu
                                        int patientNumber = userInputMenuInput.nextInt();
                                        queryString = "SELECT patients.patient_id,\n" +
                                                "\t       patients.patient_first_name,\n" +
                                                "\t       patients.patient_last_name,\n" +
                                                "\t       treatments.associated_diagnosis,\n" +
                                                "\t       patient_rooms.start_timestamp\n" +
                                                "\tFROM (((patient_treatment_components    patient_treatment_components\n" +
                                                "\t\tINNER JOIN treatment_components treatment_components\n" +
                                                "\t\t   ON (patient_treatment_components.treatment_component_id =\n" +
                                                "\t\t\t  treatment_components.treatment_component_id))\n" +
                                                "\t       INNER JOIN patients patients\n" +
                                                "\t\t  ON (patient_treatment_components.patient_id = patients.patient_id))\n" +
                                                "\t      INNER JOIN patient_rooms patient_rooms\n" +
                                                "\t\t ON (patient_rooms.patient_id = patients.patient_id))\n" +
                                                "\t     INNER JOIN treatments treatments\n" +
                                                "\t\tON (treatment_components.treatment_id = treatments.treatment_id)\n" +
                                                "\tWHERE   patients.patient_id = '" + patientNumber + "'\n" +
                                                "\t;\n";
                                        break;
                                }

                                app.runQuery(queryString);
                                break;
                            case 8:
                                System.out.println("2.8. For a given patient (either patient identification number or name), list all treatments that were\n" +
                                        "administered. Group treatments by admissions. List admissions in descending chronological order,\n" +
                                        "and list treatments in ascending chronological order within each admission. \n");
                                System.out.println("1: Give Patient Name\n"
                                        + "2: Give Patient Identification Number");
                                choice = userInputMenuInput.nextInt();
                                switch (choice) {
                                    case 1:
                                        System.out.println("Patient Name?: ");
                                        userInputMenuInput.nextLine(); //throw away the \n not consumed by nextInt() from previous menu
                                        String patientName = userInputMenuInput.nextLine();
                                        queryString = "SELECT patients.patient_id,\n" +
                                                "\t       patients.patient_first_name,\n" +
                                                "\t       patients.patient_last_name,\n" +
                                                "\t       patient_rooms.start_timestamp,\n" +
                                                "\t       treatments.treatment_short_description,\n" +
                                                "\t       patient_treatment_components.treatment_start_timestamp\n" +
                                                "\tFROM (((patient_treatment_components    patient_treatment_components\n" +
                                                "\t\tINNER JOIN treatment_components treatment_components\n" +
                                                "\t\t   ON (patient_treatment_components.treatment_component_id =\n" +
                                                "\t\t\t  treatment_components.treatment_component_id))\n" +
                                                "\t       INNER JOIN patients patients\n" +
                                                "\t\t  ON (patient_treatment_components.patient_id = patients.patient_id))\n" +
                                                "\t      INNER JOIN patient_rooms patient_rooms\n" +
                                                "\t\t ON (patient_rooms.patient_id = patients.patient_id))\n" +
                                                "\t     INNER JOIN treatments treatments\n" +
                                                "\t\tON (treatment_components.treatment_id = treatments.treatment_id)\n" +
                                                "\tWHERE   lower(patients.patient_last_name)      = lower('" + patientName + "')\n" +
                                                "\tORDER BY patient_rooms.start_timestamp DESC,\n" +
                                                "\t\t patient_treatment_components.treatment_start_timestamp ASC\n" +
                                                "\t;\n";
                                        app.runQuery(queryString);
                                        break;
                                    case 2:
                                        System.out.println("Patient Identification Number?: ");
                                        int patientNumber = userInputMenuInput.nextInt();
                                        queryString = "SELECT patients.patient_id,\n" +
                                                "\t       patients.patient_first_name,\n" +
                                                "\t       patients.patient_last_name,\n" +
                                                "\t       patient_rooms.start_timestamp,\n" +
                                                "\t       treatments.treatment_short_description,\n" +
                                                "\t       patient_treatment_components.treatment_start_timestamp\n" +
                                                "\tFROM (((patient_treatment_components    patient_treatment_components\n" +
                                                "\t\tINNER JOIN treatment_components treatment_components\n" +
                                                "\t\t   ON (patient_treatment_components.treatment_component_id =\n" +
                                                "\t\t\t  treatment_components.treatment_component_id))\n" +
                                                "\t       INNER JOIN patients patients\n" +
                                                "\t\t  ON (patient_treatment_components.patient_id = patients.patient_id))\n" +
                                                "\t      INNER JOIN patient_rooms patient_rooms\n" +
                                                "\t\t ON (patient_rooms.patient_id = patients.patient_id))\n" +
                                                "\t     INNER JOIN treatments treatments\n" +
                                                "\t\tON (treatment_components.treatment_id = treatments.treatment_id)\n" +
                                                "\tWHERE   patients.patient_id = '" + patientNumber + "'\t\t\n" +
                                                "\tORDER BY patient_rooms.start_timestamp DESC,\n" +
                                                "\t\t patient_treatment_components.treatment_start_timestamp ASC\n" +
                                                "\t;\n";
                                        app.runQuery(queryString);
                                        break;
                                }
                                break;
                            case 9:
                                System.out.println("2.9. List patients who were admitted to the hospital within 30 days of their last discharge date. For each \n"
                                        + "patient list their patient identification number, name, diagnosis, and admitting doctor. \n");
                                queryString = "SELECT patients.patient_id,\n" +
                                        "\t       patients.patient_first_name,\n" +
                                        "\t       patients.patient_last_name,\n" +
                                        "\t       treatments.associated_diagnosis,\n" +
                                        "\t       workers.worker_last_name as admitting_physician_name\n" +
                                        "\tFROM patient_treatment_components    patient_treatment_components\n" +
                                        "\t\tINNER JOIN treatment_components treatment_components\n" +
                                        "\t\t   ON patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\t  treatment_components.treatment_component_id\n" +
                                        "\t       INNER JOIN patients patients\n" +
                                        "\t\t  ON patient_treatment_components.patient_id = patients.patient_id\n" +
                                        "\t      INNER JOIN patient_rooms patient_rooms\n" +
                                        "\t\t ON patient_rooms.patient_id = patients.patient_id\n" +
                                        "\t      INNER JOIN patient_rooms patient_rooms2\n" +
                                        "\t\t ON patient_rooms2.patient_id = patients.patient_id\t\t \n" +
                                        "\t      INNER JOIN workers workers\n" +
                                        "\t\t ON workers.worker_id = patient_rooms.admitting_physician_id\n" +
                                        "\t     INNER JOIN treatments treatments\n" +
                                        "\t\tON treatment_components.treatment_id = treatments.treatment_id\n" +
                                        "\tWHERE CAST(JULIANDAY(patient_rooms.start_timestamp) - JULIANDAY(patient_rooms2.end_timestamp) AS INTEGER) < 30\n" +
                                        "\t;\n";
                                app.runQuery(queryString);
                                break;
                            case 10:
                                System.out.println("2.10. For each patient that has ever been admitted to the hospital, list their total number of\n" +
                                        "admissions, average duration of each admission, longest span between admissions, shortest span\n" +
                                        "between admissions, and average span between admissions. ");
                                queryString = "SELECT patients.patient_id,\n" +
                                        "\t       COUNT(patient_rooms.patient_room_id) AS total_admissions,\n" +
                                        "\t       AVG(CAST(JULIANDAY(patient_rooms.end_timestamp) - JULIANDAY(patient_rooms.start_timestamp) AS INTEGER)) as average_duration,\n" +
                                        "\t       MAX(CAST(JULIANDAY(patient_rooms2.start_timestamp) - JULIANDAY(patient_rooms.end_timestamp) AS INTEGER)) as longest_span_between_admissions,\n" +
                                        "\t       MIN(CAST(JULIANDAY(patient_rooms2.start_timestamp) - JULIANDAY(patient_rooms.end_timestamp) AS INTEGER)) as shortest_span_between_admissions,   \n" +
                                        "\t       AVG(CAST(JULIANDAY(patient_rooms2.start_timestamp) - JULIANDAY(patient_rooms.end_timestamp) AS INTEGER)) as average_span_between_admissions   \n" +
                                        "\tFROM patient_treatment_components    patient_treatment_components\n" +
                                        "\t\tINNER JOIN treatment_components treatment_components\n" +
                                        "\t\t   ON patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\t  treatment_components.treatment_component_id\n" +
                                        "\t       INNER JOIN patients patients\n" +
                                        "\t\t  ON patient_treatment_components.patient_id = patients.patient_id\n" +
                                        "\t      INNER JOIN patient_rooms patient_rooms\n" +
                                        "\t\t ON patient_rooms.patient_id = patients.patient_id\n" +
                                        "\t      INNER JOIN patient_rooms patient_rooms2\n" +
                                        "\t\t ON patient_rooms2.patient_id = patients.patient_id         \n" +
                                        "\t     INNER JOIN treatments treatments\n" +
                                        "\t\tON treatment_components.treatment_id = treatments.treatment_id\n" +
                                        "\tGROUP BY patients.patient_id\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            default:
                                if (choice != 1 || choice != 2 || choice != 3 || choice != 4 || choice != 5 || choice != 6 || choice != 7 || choice != 8 || choice != 9 || choice != 10)
                                    System.out.println("\t*** Please enter a valid choice ***\n");
                                break;
                        }

                    } catch (Exception e) {
                        System.out.println("\t\"" + choice
                                + "\" not found\n");
                    }
                    break;
                case 3:
                    System.out.println("Enter Operation Code: \n"
                            + "1: List diagnoses of all admitted patients"
                            + "2: List diagnoses of all outpatients\n"
                            + "3: List all diagnoses\n"
                            + "4: All treatments\n"
                            + "5: Admitted patient treatments\n"
                            + "6: Outpatient treatments\n"
                            + "7: Top 5 patient diagnoses\n"
                            + "8: List treatment doctors\n");
                    try {
                        choice = userInputMenuInput.nextInt();
                        switch (choice) {
                            case 1:
                                System.out.println("3.1. List the diagnoses given to admitted patients, in descending order of occurrences. List diagnosis\n" +
                                        "identification number, name, and total occurrences of each diagnosis. ");
                                queryString = "SELECT treatments.treatment_id              AS diagnosis_id,\n" +
                                        "\t       treatments.associated_diagnosis      AS name,\n" +
                                        "\t       COUNT(patient_rooms.patient_room_id) AS total_occurences\n" +
                                        "\tFROM (((patient_treatment_components    patient_treatment_components\n" +
                                        "\t\tINNER JOIN treatment_components treatment_components\n" +
                                        "\t\t   ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\t  treatment_components.treatment_component_id))\n" +
                                        "\t       INNER JOIN patients patients\n" +
                                        "\t\t  ON (patient_treatment_components.patient_id = patients.patient_id))\n" +
                                        "\t      INNER JOIN patient_rooms patient_rooms\n" +
                                        "\t\t ON (patient_rooms.patient_id = patients.patient_id))\n" +
                                        "\t     INNER JOIN treatments treatments\n" +
                                        "\t\tON (treatment_components.treatment_id = treatments.treatment_id)\n" +
                                        "\tORDER BY 3 DESC\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 2:
                                System.out.println("3.2. List the diagnoses given to outpatients, in descending order of occurrences. List diagnosis\n" +
                                        "identification number, name, and total occurrences of each diagnosis.");
                                queryString = "SELECT treatments.treatment_id              AS diagnosis_id,\n" +
                                        "\t       treatments.associated_diagnosis      AS name,\n" +
                                        "\t       COUNT(patient_rooms.patient_room_id) AS total_occurences\n" +
                                        "\tFROM ((((treatment_components    treatment_components\n" +
                                        "\t\t INNER JOIN treatments treatments\n" +
                                        "\t\t    ON (treatment_components.treatment_id = treatments.treatment_id))\n" +
                                        "\t\tINNER JOIN\n" +
                                        "\t\tpatient_treatment_components patient_treatment_components\n" +
                                        "\t\t   ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\t  treatment_components.treatment_component_id))\n" +
                                        "\t       INNER JOIN patients patients\n" +
                                        "\t\t  ON (patient_treatment_components.patient_id = patients.patient_id))\n" +
                                        "\t      INNER JOIN patient_rooms patient_rooms\n" +
                                        "\t\t ON (patient_rooms.patient_id = patients.patient_id))\n" +
                                        "\t     INNER JOIN services services\n" +
                                        "\t\tON (treatment_components.service_id = services.service_id)\n" +
                                        "\tWHERE (services.service_type = 'outpatient')\n" +
                                        "\tORDER BY 3 DESC\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 3:
                                System.out.println("3.3. List the diagnoses given to hospital patients (both inpatient and outpatient), in descending order of\n" +
                                        "occurrences. List diagnosis identification number, name, and total occurrences of each diagnosis. ");
                                queryString = "SELECT treatments.treatment_id         AS diagnosis_id,\n" +
                                        "\t       treatments.associated_diagnosis AS name,\n" +
                                        "\t       COUNT(patient_treatment_components.patient_treatment_component_id)\n" +
                                        "\t\t  AS total_occurences\n" +
                                        "\tFROM ((treatment_components    treatment_components\n" +
                                        "\t       INNER JOIN treatments treatments\n" +
                                        "\t\t  ON (treatment_components.treatment_id = treatments.treatment_id))\n" +
                                        "\t      INNER JOIN\n" +
                                        "\t      patient_treatment_components patient_treatment_components\n" +
                                        "\t\t ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\ttreatment_components.treatment_component_id))\n" +
                                        "\t     INNER JOIN patients patients\n" +
                                        "\t\tON (patient_treatment_components.patient_id = patients.patient_id)\n" +
                                        "\tORDER BY 3 DESC\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 4:
                                System.out.println("3.4. List the treatments performed at the hospital (to both inpatients and outpatients), in descending order\n" +
                                        "of occurrences. List treatment identification number, name, and total number of occurrences of each\n" +
                                        "treatment");
                                queryString = "SELECT treatments.treatment_id,\n" +
                                        "\t       treatments.treatment_short_description,\n" +
                                        "\t       COUNT(patient_treatment_components.patient_treatment_component_id)\n" +
                                        "\t\t  AS total_occurences\n" +
                                        "\tFROM ((treatment_components    treatment_components\n" +
                                        "\t       INNER JOIN treatments treatments\n" +
                                        "\t\t  ON (treatment_components.treatment_id = treatments.treatment_id))\n" +
                                        "\t      INNER JOIN\n" +
                                        "\t      patient_treatment_components patient_treatment_components\n" +
                                        "\t\t ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\ttreatment_components.treatment_component_id))\n" +
                                        "\t     INNER JOIN patients patients\n" +
                                        "\t\tON (patient_treatment_components.patient_id = patients.patient_id)\n" +
                                        "\tORDER BY 3 DESC\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 5:
                                System.out.println("3.5. List the treatments performed on admitted patients, in descending order of occurrences. List treatment\n" +
                                        "identification number, name, and total number of occurrences of each treatment. \n");
                                queryString = "SELECT treatments.treatment_id,\n" +
                                        "\t       treatments.treatment_short_description,\n" +
                                        "\t       COUNT(patient_treatment_components.patient_treatment_component_id)\n" +
                                        "\t\t  AS total_occurences\n" +
                                        "\tFROM (((treatment_components    treatment_components\n" +
                                        "\t\tINNER JOIN treatments treatments\n" +
                                        "\t\t   ON (treatment_components.treatment_id = treatments.treatment_id))\n" +
                                        "\t       INNER JOIN\n" +
                                        "\t       patient_treatment_components patient_treatment_components\n" +
                                        "\t\t  ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\t treatment_components.treatment_component_id))\n" +
                                        "\t      INNER JOIN patients patients\n" +
                                        "\t\t ON (patient_treatment_components.patient_id = patients.patient_id))\n" +
                                        "\t     INNER JOIN services services\n" +
                                        "\t\tON (treatment_components.service_id = services.service_id)\n" +
                                        "\tWHERE (services.service_type = 'inpatient')\n" +
                                        "\tORDER BY 3 DESC\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 6:
                                System.out.println("3.6. List the treatments performed on outpatients, in descending order of occurrences. List treatment\n" +
                                        "identification number, name, and total number of occurrences of each treatment. ");
                                queryString = "SELECT treatments.treatment_id,\n" +
                                        "\t       treatments.treatment_short_description,\n" +
                                        "\t       COUNT(patient_treatment_components.patient_treatment_component_id)\n" +
                                        "\t\t  AS total_occurences\n" +
                                        "\tFROM (((treatment_components    treatment_components\n" +
                                        "\t\tINNER JOIN treatments treatments\n" +
                                        "\t\t   ON (treatment_components.treatment_id = treatments.treatment_id))\n" +
                                        "\t       INNER JOIN\n" +
                                        "\t       patient_treatment_components patient_treatment_components\n" +
                                        "\t\t  ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\t treatment_components.treatment_component_id))\n" +
                                        "\t      INNER JOIN patients patients\n" +
                                        "\t\t ON (patient_treatment_components.patient_id = patients.patient_id))\n" +
                                        "\t     INNER JOIN services services\n" +
                                        "\t\tON (treatment_components.service_id = services.service_id)\n" +
                                        "\tWHERE (services.service_type = 'outpatient')\n" +
                                        "\tORDER BY 3 DESC\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 7:
                                System.out.println("3.7. List the diagnoses associated with the top 5 patients who have the highest occurrences of admissions\n" +
                                        "to the hospital, in ascending order or correlation. ");
                                queryString = "SELECT treatments.associated_diagnosis AS diagnosis\n" +
                                        "\t     , COUNT(patient_rooms.patient_id) as total_occurences\n" +
                                        "\tFROM (((patient_rooms    patient_rooms\n" +
                                        "\t\tINNER JOIN patients patients\n" +
                                        "\t\t   ON (patient_rooms.patient_id = patients.patient_id))\n" +
                                        "\t       INNER JOIN\n" +
                                        "\t       patient_treatment_components patient_treatment_components\n" +
                                        "\t\t  ON (patient_treatment_components.patient_id = patients.patient_id))\n" +
                                        "\t      INNER JOIN treatment_components treatment_components\n" +
                                        "\t\t ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\ttreatment_components.treatment_component_id))\n" +
                                        "\t     INNER JOIN treatments treatments\n" +
                                        "\t\tON (treatment_components.treatment_id = treatments.treatment_id)\n" +
                                        "\tORDER BY COUNT(patient_rooms.patient_id) DESC, associated_diagnosis ASC\n" +
                                        "\tLIMIT 5;";
                                app.runQuery(queryString);
                                break;
                            case 8:
                                System.out.println("3.8. For a given treatment occurrence, list all the doctors that were involved. Also include the patient name\n" +
                                        "and the doctor who ordered the treatment. ");
                                System.out.println("Treatment?: ");
                                userInputMenuInput.nextLine(); //throw away the \n not consumed by nextInt() from previous menu
                                String treatmentType = userInputMenuInput.nextLine();
                                queryString = "SELECT treatments.treatment_id,   \n" +
                                        "\t       treatments.treatment_short_description,\t\n" +
                                        "\t       patient_treatment_components.patient_treatment_component_id  AS treatment_occurence_id,\n" +
                                        "\t       workers.worker_last_name AS ordering_physician_last_name,\n" +
                                        "\t       patients.patient_last_name,\n" +
                                        "\t       patients.patient_first_name\n" +
                                        "\tFROM (((patient_treatment_components    patient_treatment_components\n" +
                                        "\t\tINNER JOIN treatment_components treatment_components\n" +
                                        "\t\t   ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\t  treatment_components.treatment_component_id))\n" +
                                        "\t       INNER JOIN patients patients\n" +
                                        "\t\t  ON (patient_treatment_components.patient_id = patients.patient_id))\n" +
                                        "\t      INNER JOIN workers workers\n" +
                                        "\t\t ON (patient_treatment_components.ordering_physician_id =\n" +
                                        "\t\t\tworkers.worker_id))\n" +
                                        "\t     INNER JOIN treatments treatments\n" +
                                        "\t\tON (treatment_components.treatment_id = treatments.treatment_id)\n" +
                                        "\tWHERE lower(treatments.treatment_short_description) LIKE lower('" + treatmentType + "%')\t\t\n" +
                                        "\t\t;";
                                app.runQuery(queryString);
                                //"\t  AND lower(workers.worker_last_name) = lower('"+doctorName+"')\n" +
                                break;
                            default:
                                if (choice != 1 || choice != 2 || choice != 3 || choice != 4 || choice != 5 || choice != 6 || choice != 7 || choice != 8)
                                    System.out.println("\t*** Please enter a valid choice ***\n");
                                break;
                        }
                    } catch (Exception e) {
                        System.out.println("\t\"" + choice
                                + "\" not found\n");
                    }

                    break;
                case 4:
                    System.out.println("Enter Operation Code: \n"
                            + "1: All employees\n"
                            + "2: Primary doctors of high admission rate patients\n"
                            + "3: Doctor's diagnoses\n"
                            + "4: Doctor's treatments ordered\n"
                            + "5: Doctor treatment participation\n"
                            + "6: All admitted patient doctors\n");
                    try {
                        choice = userInputMenuInput.nextInt();
                        switch (choice) {
                            case 1:
                                System.out.println("4.1. List all workers at the hospital, in ascending last name, first name order. For each worker, list their,\n" +
                                        "name, and job category. ");
                                queryString = "SELECT workers.worker_last_name,\n" +
                                        "\t       workers.worker_first_name,\n" +
                                        "\t       worker_types.worker_type_description as job_category\n" +
                                        "\tFROM workers    workers\n" +
                                        "\t     INNER JOIN worker_types worker_types\n" +
                                        "\t\tON (workers.worker_type_id = worker_types.worker_type_id)\n" +
                                        "\tORDER BY workers.worker_last_name ASC, workers.worker_first_name ASC\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 2:
                                System.out.println("4.2. List the primary doctors of patients with a high admission rate (at least 4 admissions within a one-year time frame).");
                                queryString = "SELECT patients.primary_physician_last_name\n" +
                                        "\tFROM (((treatment_components    treatment_components\n" +
                                        "\t\tINNER JOIN treatments treatments\n" +
                                        "\t\t   ON (treatment_components.treatment_id = treatments.treatment_id))\n" +
                                        "\t       INNER JOIN\n" +
                                        "\t       patient_treatment_components patient_treatment_components\n" +
                                        "\t\t  ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\t treatment_components.treatment_component_id))\n" +
                                        "\t      INNER JOIN patients patients\n" +
                                        "\t\t ON (patient_treatment_components.patient_id = patients.patient_id))\n" +
                                        "\t     INNER JOIN patient_rooms patient_rooms\n" +
                                        "\t\tON (patient_rooms.patient_id = patients.patient_id)\n" +
                                        "\tWHERE CAST(JULIANDAY(patient_rooms.start_timestamp) AS INTEGER) < 365\n" +
                                        "\tGROUP BY patient_rooms.patient_id HAVING count(*) > 4\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            case 3:
                                System.out.println("4.3. For a given doctor, list all associated diagnoses in descending order of occurrence. For each\n" +
                                        "diagnosis, list the total number of occurrences for the given doctor.");
                                System.out.println("Doctor Name?: ");
                                userInputMenuInput.nextLine(); //throw away the \n not consumed by nextInt() from previous menu
                                String doctorName = userInputMenuInput.next();
                                queryString = "SELECT workers.worker_id,\n" +
                                        "\t       workers.worker_last_name,\n" +
                                        "\t       treatments.associated_diagnosis AS diagnosis,\n" +
                                        "\t       COUNT(patient_treatment_components.patient_treatment_component_id)\n" +
                                        "\t\t  AS total_occurences\n" +
                                        "\tFROM (((patient_treatment_components    patient_treatment_components\n" +
                                        "\t\tINNER JOIN workers workers\n" +
                                        "\t\t   ON (patient_treatment_components.ordering_physician_id =\n" +
                                        "\t\t\t  workers.worker_id))\n" +
                                        "\t       INNER JOIN treatment_components treatment_components\n" +
                                        "\t\t  ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\t treatment_components.treatment_component_id))\n" +
                                        "\t      INNER JOIN treatments treatments\n" +
                                        "\t\t ON (treatment_components.treatment_id = treatments.treatment_id))\n" +
                                        "\t     INNER JOIN worker_types worker_types\n" +
                                        "\t\tON (workers.worker_type_id = worker_types.worker_type_id)\n" +
                                        "\tWHERE (worker_types.worker_type_code = 'D')\n" +
                                        "\t  AND lower(workers.worker_last_name) = lower('" + doctorName + "')\n" +
                                        "\tGROUP BY 1\n" +
                                        "\tORDER BY 4 DESC\n" +
                                        "     \t;";
                                app.runQuery(queryString);
                                break;
                            case 4:
                                System.out.println("4.4. For a given doctor, list all treatments that they ordered in descending order of occurrence. For each\n" +
                                        "treatment, list the total number of occurrences for the given doctor.");
                                System.out.println("Doctor Last Name?: ");
                                userInputMenuInput.nextLine(); //throw away the \n not consumed by nextInt() from previous menu
                                doctorName = userInputMenuInput.nextLine();
                                queryString = "SELECT workers.worker_id,\n" +
                                        "\t       workers.worker_last_name,\n" +
                                        "\t       treatments.treatment_short_description,\n" +
                                        "\t       COUNT(patient_treatment_components.patient_treatment_component_id)\n" +
                                        "\t\t  AS total_occurences\n" +
                                        "\tFROM (((patient_treatment_components    patient_treatment_components\n" +
                                        "\t\tINNER JOIN workers workers\n" +
                                        "\t\t   ON (patient_treatment_components.ordering_physician_id =\n" +
                                        "\t\t\t  workers.worker_id))\n" +
                                        "\t       INNER JOIN treatment_components treatment_components\n" +
                                        "\t\t  ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\t treatment_components.treatment_component_id))\n" +
                                        "\t      INNER JOIN treatments treatments\n" +
                                        "\t\t ON (treatment_components.treatment_id = treatments.treatment_id))\n" +
                                        "\t     INNER JOIN worker_types worker_types\n" +
                                        "\t\tON (workers.worker_type_id = worker_types.worker_type_id)\n" +
                                        "\tWHERE (worker_types.worker_type_code = 'D')\n" +
                                        "\t  AND lower(workers.worker_last_name) = lower('" + doctorName + "')\n" +
                                        "\tGROUP BY 1\n" +
                                        "\tORDER BY 4 DESC\n" +
                                        "     \t;";
                                app.runQuery(queryString);
                                break;
                            case 5:
                                System.out.println("4.5. For a given doctor, list all treatments in which they participated, in descending order of occurrence. For\n" +
                                        "each treatment, list the total number of occurrences for the given doctor.");
                                System.out.println("Doctor Last Name?: ");
                                userInputMenuInput.nextLine(); //throw away the \n not consumed by nextInt() from previous menu
                                doctorName = userInputMenuInput.nextLine();
                                queryString = "\tSELECT workers.worker_id as doctor_id,\n" +
                                        "\t       workers.worker_last_name as doctor_name,\n" +
                                        "\t       treatments.treatment_short_description,\n" +
                                        "\t       COUNT(patient_treatment_components.patient_treatment_component_id)\n" +
                                        "\t\t  AS total_occurences\n" +
                                        "\tFROM (((patient_treatment_components    patient_treatment_components\n" +
                                        "\t\tINNER JOIN workers workers\n" +
                                        "\t\t   ON (patient_treatment_components.ordering_physician_id =\n" +
                                        "\t\t\t  workers.worker_id))\n" +
                                        "\t       INNER JOIN treatment_components treatment_components\n" +
                                        "\t\t  ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\t treatment_components.treatment_component_id))\n" +
                                        "\t      INNER JOIN treatments treatments\n" +
                                        "\t\t ON (treatment_components.treatment_id = treatments.treatment_id))\n" +
                                        "\t     INNER JOIN worker_types worker_types\n" +
                                        "\t\tON (workers.worker_type_id = worker_types.worker_type_id)\n" +
                                        "\tWHERE (worker_types.worker_type_code = 'D')\n" +
                                        "\t  AND lower(workers.worker_last_name) = lower('" + doctorName + "')\n" +
                                        "\tGROUP BY 3\n" +
                                        "\tORDER BY 4 DESC";
                                app.runQuery(queryString);
                                break;
                            case 6:
                                System.out.println("4.6. List doctors who have been involved in the treatment of every admitted patient. \n");
                                queryString = "SELECT workers.worker_id,\n" +
                                        "\t       workers.worker_last_name,\n" +
                                        "\t       treatments.treatment_short_description,\n" +
                                        "\t       patients.patient_id\n" +
                                        "\tFROM (((((workers    workers\n" +
                                        "\t\t  INNER JOIN worker_types worker_types\n" +
                                        "\t\t     ON (workers.worker_type_id = worker_types.worker_type_id))\n" +
                                        "\t\t INNER JOIN\n" +
                                        "\t\t patient_treatment_components patient_treatment_components\n" +
                                        "\t\t    ON (patient_treatment_components.ordering_physician_id =\n" +
                                        "\t\t\t   workers.worker_id))\n" +
                                        "\t\tINNER JOIN treatment_components treatment_components\n" +
                                        "\t\t   ON (patient_treatment_components.treatment_component_id =\n" +
                                        "\t\t\t  treatment_components.treatment_component_id))\n" +
                                        "\t       INNER JOIN treatments treatments\n" +
                                        "\t\t  ON (treatment_components.treatment_id = treatments.treatment_id))\n" +
                                        "\t      INNER JOIN patients patients\n" +
                                        "\t\t ON (patient_treatment_components.patient_id = patients.patient_id))\n" +
                                        "\t     INNER JOIN patient_rooms patient_rooms\n" +
                                        "\t\tON (patient_rooms.patient_id = patients.patient_id)\n" +
                                        "\tWHERE (worker_types.worker_type_code = 'D')\n" +
                                        "\t;";
                                app.runQuery(queryString);
                                break;
                            default:
                                if (choice != 1 || choice != 2 || choice != 3 || choice != 4 || choice != 5 || choice != 6)
                                    System.out.println("\t*** Please enter a valid choice ***\n");
                                break;
                        }
                    } catch (Exception e) {
                        System.out.println("\t\"" + choice
                                + "\" not found\n");
                    }
                    break;
                case 5:
                    System.out.println("Enter Personnel File Name: ");
                    Scanner importFileName;
                    importFileName = new Scanner(System.in);
                    String fileName = importFileName.nextLine();
                    break;
                case 6:
                    System.out.println("Enter Treatment File Name: ");
                    Scanner importTreatmentFileName;
                    importTreatmentFileName = new Scanner(System.in);
                    String treatmentFileName = importTreatmentFileName.nextLine();
                    app.importTreatmentData(treatmentFileName);
                    break;
                case 7:
                    System.out.println("Deleting and recreating database....");
                    try {
                        app.deleteDatabase();
                        app.createDatabase();
                    } catch (Exception e) {
                        System.out.println("Main exception: " + e.getMessage());
                        e.printStackTrace();
                    }
                    System.out.println("Database recreated.");
                    break;

                case 0:
                    System.out.println("");
                    break;
                default:
                    if (choice != 1 || choice != 2 || choice != 3 || choice != 4 || choice != 5 || choice != 6 || choice != 7)
                        System.out.println("\t*** Please enter a valid choice ***\n");
                    break;
            }
        }
        while (choice != 0);
    }


    public void Hospital(String filename) {
        String queryString = "";
        System.out.println("Hospital created");
    }

    public void loadData(String queryString) {
        //load Data
        System.out.println("loadData");
    }

    public void occupiedRooms() throws Exception {
        String queryString = "SELECT rooms.room_number, patients.patient_first_name, patients.patient_last_name, patient_rooms.start_timestamp, patient_rooms.end_timestamp FROM (patient_rooms    patient_rooms INNER JOIN patients patient ON (patient_rooms.patient_id = patients.patient_id)) INNER JOIN rooms rooms ON (patient_rooms.room_id = rooms.room_id);";
        try (Connection conn = this.connect();) {
            PreparedStatement ps101 = conn.prepareStatement(queryString);
            ps101.executeUpdate();
            ps101.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception occupiedRooms:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void importPeopleData(String importPeopleDataFilename) throws Exception {
        //1. Get the data from the file
        String input = "";
        String defaultString = "";
        int counter = 0;
        int attemptCounter = 0;
        String previousString = "";
        String queryString = "";
        Statement stmt = null;
        String[] items = new String[14];
        try {
            Scanner s = new Scanner(new File(importPeopleDataFilename));

            while (s.hasNextLine()) {
                // go get another line of data
                input = s.nextLine();
                if (input != null) {
                    // System.out.println(input);
                    items = input.split(",", -1);
                    attemptCounter++;
                    //check that first field is valid
                    //System.out.println(items[0]);

                    //1. Insert into import person table queryString
                    queryString = "REPLACE INTO import_person_data (person_type, person_first_name, person_last_name, privilege_type, patient_id, room_number, emergency_contact_name, emergency_contact_number, insurance_policy_number, insurance_policy_company, primary_physician_last_name, initial_diagnosis, admission_date, discharge_date) \n"
                            + " VALUES \n"
                            + " (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

                    Connection conn = this.connect();
                    try {
                        PreparedStatement ps = conn.prepareStatement(queryString);
                        for (int j = 1; j < 15; j++) {
                            ps.setString(j, items[j - 1]);
                        }
                        ps.executeUpdate();
                        ps.close();
                        counter++;

                    } catch (SQLException e) {
                        System.out.println("SQL import_person_data Exception: " + e.getMessage());
                        //System.out.println("queryString: " + queryString);
                    }
                    //System.out.println("queryString: " + queryString);

                    int personCounter = 0;
                    //Volunteer, Doctor, Nurse, Technician, Administrator
                    if (items[0].equals("V")
                            || items[0].equals("D")
                            || items[0].equals("N")
                            || items[0].equals("T")
                            || items[0].equals("A")
                    ) {
                        personCounter = 0;
                        //System.out.println("see if worker person is there or not: ");
                        //
                        try {
                            // execute statement and pull back data
                            //System.out.println("index 2: " + items[2]);
                            stmt = conn.createStatement();
                            queryString = "SELECT COUNT(*) as personCounter FROM workers WHERE lower(worker_last_name)=lower('" + items[2] + "') \n";
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                personCounter = rs.getInt("personCounter");
                                //System.out.println("personCounter: " + personCounter);
                            }
                            stmt.close();
                        } catch (SQLException e) {
                            System.out.println("SQL Exception - personCounter: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                            //System.out.println("worker_last_name: " + items[2]);
                            //System.out.println("personCounter: " + personCounter);
                        }
                        //System.out.println("Worker person data: ");
                        //for (int i=0; i<items.length; i++){
                        //    System.out.println("items["+i+"]: " + items[i]);
                        //}
                        //if person is not there, add it
                        if (personCounter < 1) {
                            //System.out.println("Worker person is not there, so add it: ");
                            queryString = "INSERT INTO workers (worker_first_name, worker_last_name, worker_type_id)  \n"
                                    + " VALUES \n"
                                    + " (?,?,(SELECT worker_type_id FROM worker_types WHERE worker_type_code=?));";
                            try {
                                PreparedStatement ps1 = conn.prepareStatement(queryString);
                                ps1.setString(1, items[1]);
                                ps1.setString(2, items[2]);
                                ps1.setString(3, items[0]);

                                ps1.executeUpdate();
                                ps1.close();
                            } catch (SQLException e) {
                                System.out.println("SQL Exception - Insert worker: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                                //System.out.println("worker_first_name: " + items[1]);
                                //System.out.println("worker_last_name: " + items[2]);
                                //System.out.println("worker_type_id: " + items[0]);

                            }//end try
                        } else { //update record if it is already there
                            queryString = "UPDATE workers \n"
                                    + " SET worker_first_name = ?  \n"
                                    + "   , worker_type_id = (SELECT worker_type_id FROM worker_types WHERE worker_type_code=?) \n"
                                    + " WHERE worker_last_name = ?  \n"
                            ;
                            try {
                                PreparedStatement ps1 = conn.prepareStatement(queryString);
                                ps1.setString(1, items[1]);
                                ps1.setString(2, items[2]);
                                ps1.setString(3, items[0]);

                                ps1.executeUpdate();
                                ps1.close();
                            } catch (SQLException e) {
                                System.out.println("SQL Exception - Update worker: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                                //System.out.println("worker_first_name: " + items[1]);
                                //System.out.println("worker_last_name: " + items[2]);
                                //System.out.println("worker_type_id: " + items[0]);

                            }//end try
                        }
                        //System.out.println("Worker person has been added: ");
                        //for (int i=0; i<items.length; i++){
                        //    System.out.println("items["+i+"]: " + items[i]);
                        //}
                    }//end if worker insert/update

                    //Doctor
                    if (items[0].equals("D")) {
                        //Consulting privilege
                        if (items[3].equals("C")) {
                            queryString = "UPDATE workers SET  \n"
                                    + " consulting_privilege = 'Y'  \n"
                                    + " WHERE worker_last_name = '" + items[2] + "'  \n"
                                    + " ;  \n"
                            ;
                        }
                        //Admitting Privilege
                        else if (items[3].equals("A")) {
                            queryString = "UPDATE workers SET  \n"
                                    + " admitting_privilege = 'Y'  \n"
                                    + " WHERE worker_last_name = '" + items[2] + "'  \n"
                                    + " ;  \n"
                            ;
                        }
                        //Update workers table with C/A privilege
                        try {
                            PreparedStatement ps3 = conn.prepareStatement(queryString);
                            ps3.executeUpdate();
                            ps3.close();
                        } catch (SQLException e) {
                            System.out.println("SQL Exception - Worker Hospital Privileges: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                        }
                        //System.out.println("queryString: " + queryString);
                    } // end if Doctor

                    // Patients and associated Doctors
                    int patientId = 0;
                    int doctorId = 0;

                    //Outpatient, Inpatient
                    if (items[0].equals("O")
                            || items[0].equals("I")
                    ) {
                        personCounter = 0;
                        //System.out.println("see if patient is there or not: ");
                        //
                        try {
                            // execute statement and pull back data
                            //System.out.println("index 2: " + items[2]);
                            stmt = conn.createStatement();
                            queryString = "SELECT COUNT(*) as personCounter FROM patients WHERE lower(patient_last_name)=lower('" + items[2] + "') \n";
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                personCounter = rs.getInt("personCounter");
                                //System.out.println("personCounter: " + personCounter);
                            }
                            stmt.close();
                        } catch (SQLException e) {
                            System.out.println("SQL Exception - personCounter: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                            //System.out.println("patient_last_name: " + items[2]);
                            //System.out.println("personCounter: " + personCounter);
                        }
                        //System.out.println("Worker person data: ");
                        //for (int i=0; i<items.length; i++){
                        //    System.out.println("items["+i+"]: " + items[i]);
                        //}
                        //if person is not there, add it
                        if (personCounter < 1) {
                            //System.out.println("Patient is not there, so add it: ");
                            queryString = "INSERT INTO patients (patient_id, patient_first_name, patient_last_name, emergency_contact_name \n"
                                    + " , emergency_contact_number, insurance_policy_number, insurance_policy_company \n"
                                    + " , primary_physician_last_name) \n"
                                    + " VALUES \n"
                                    + " (?,?,?,?,?,?,?,?) "
                                    + " ;  \n"
                            ;
                            try {
                                PreparedStatement ps7 = conn.prepareStatement(queryString);
                                if (items[4].length() > 0) {
                                    ps7.setInt(1, Integer.parseInt(items[4]));

                                } else {
                                    ps7.setNull(1, Types.NULL);
                                }
                                ps7.setString(2, items[1]);
                                ps7.setString(3, items[2]);
                                ps7.setString(4, items[6]);
                                ps7.setString(5, items[7]);
                                ps7.setString(6, items[8]);
                                ps7.setString(7, items[9]);
                                ps7.setString(8, items[10]);
                                patientId = ps7.executeUpdate();
                                ps7.close();
                            } catch (SQLException e) {
                                System.out.println("SQL Exception - insert patient: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                                //System.out.println("patient_first_name: " + items[1]);
                                //System.out.println("patient_last_name: " + items[2]);
                            }

                        } else { //update record if it is already there
                            queryString = "UPDATE patients\n" +
                                    "SET\n" +
                                    "patient_first_name = ?,\n" +
                                    "emergency_contact_name = ?,\n" +
                                    "emergency_contact_number = ?,\n" +
                                    "insurance_policy_number = ?,\n" +
                                    "insurance_policy_company = ?,\n" +
                                    "primary_physician_last_name = ?\n" +
                                    "WHERE patient_last_name = ?;"
                            ;
                            try {
                                PreparedStatement ps7 = conn.prepareStatement(queryString);
                                ps7.setString(1, items[1]);
                                ps7.setString(2, items[6]);
                                ps7.setString(3, items[7]);
                                ps7.setString(4, items[8]);
                                ps7.setString(5, items[9]);
                                ps7.setString(6, items[10]);
                                ps7.setString(7, items[2]);
                                patientId = ps7.executeUpdate();
                                ps7.close();
                            } catch (SQLException e) {
                                System.out.println("SQL Exception - Update patient: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                                //System.out.println("patient_first_name: " + items[1]);
                                //System.out.println("patient_last_name: " + items[2]);
                                //System.out.println("patient_type_id: " + items[0]);

                            }//end try
                        }


                        //check if doctor listed on patient record is in workers table
                        personCounter = 0;
                        //Volunteer, Doctor, Nurse, Technician, Administrator
                        try {
                            // execute statement and pull back data
                            //System.out.println("index 2: " + items[2]);
                            stmt = conn.createStatement();
                            queryString = "SELECT COUNT(*) as personCounter FROM workers WHERE lower(worker_last_name)=lower('" + items[10] + "') \n";
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                personCounter = rs.getInt("personCounter");
                                //System.out.println("personCounter: " + personCounter);
                            }
                            stmt.close();
                        } catch (SQLException e) {
                            System.out.println("SQL Exception - personCounter: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                            //System.out.println("worker_last_name: " + items[2]);
                            //System.out.println("personCounter: " + personCounter);
                        }
                        //System.out.println("Worker person data: ");
                        //for (int i=0; i<items.length; i++){
                        //    System.out.println("items["+i+"]: " + items[i]);
                        //}
                        //if person is not there, add it
                        if (personCounter < 1) {
                            //System.out.println("Worker person is not there, so add it: ");
                            queryString = "INSERT INTO workers (worker_last_name, worker_type_id)  \n"
                                    + " VALUES \n"
                                    + " (?,(SELECT worker_type_id FROM worker_types WHERE worker_type_code='D'));";
                            try {
                                PreparedStatement ps1 = conn.prepareStatement(queryString);
                                ps1.setString(1, items[10]);
                                ps1.executeUpdate();
                                ps1.close();
                            } catch (SQLException e) {
                                System.out.println("SQL Exception - Insert worker: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                            }//end try
                        }

                        //get doctorId
                        try {
                            // execute statement and pull back data
                            //System.out.println("index 2: " + items[2]);
                            stmt = conn.createStatement();
                            queryString = "SELECT MAX(worker_id) AS doctorId FROM workers WHERE lower(worker_last_name)=lower('" + items[10] + "') \n";
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                doctorId = rs.getInt("doctorId");
                                //System.out.println("personCounter: " + personCounter);
                            }
                            stmt.close();
                        } catch (SQLException e) {
                            System.out.println("SQL Exception - personCounter: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                            //System.out.println("worker_last_name: " + items[2]);
                            //System.out.println("personCounter: " + personCounter);
                        }

                        //get patientId
                        try {
                            // execute statement and pull back data
                            //System.out.println("index 2: " + items[2]);
                            stmt = conn.createStatement();
                            queryString = "SELECT MAX(patient_id) AS patientId FROM patients WHERE lower(patient_last_name)=lower('" + items[2] + "') \n";
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                patientId = rs.getInt("patientId");
                                //System.out.println("personCounter: " + personCounter);
                            }
                            stmt.close();
                        } catch (SQLException e) {
                            System.out.println("SQL Exception - personCounter: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                            //System.out.println("patient_last_name: " + items[2]);
                            //System.out.println("personCounter: " + personCounter);
                        }

                        //System.out.println("doctorId: " + doctorId);
                        //System.out.println("patientId: " + patientId);

                        // add patient - doctor relationship, if it is not there
                        int doctorPatientCount = 0;
                        int doctorPatientId = 0;
                        try {
                            //see if doctor - patient record is there or not
                            // execute statement and pull back data
                            stmt = conn.createStatement();
                            queryString = "SELECT COUNT(*) as doctorPatientCount FROM patient_doctors  \n"
                                    + " WHERE patient_id=" + patientId + " \n"
                                    + " AND doctor_id=" + doctorId + " \n"
                            ;
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                doctorPatientCount = rs.getInt("doctorPatientCount");
                            }
                        } catch (SQLException e) {
                            System.out.println("SQL doctorPatientCount Exception: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                        }

                        // date manipulation
                        String startDateString = "", endDateString = "", tempDateString1 = "", tempDateString2[] = null;
                        if (items[12].length() > 0) {
                            tempDateString1 = items[12];
                            tempDateString2 = tempDateString1.split("-");
                            if (tempDateString2[1].length() < 2) {
                                tempDateString2[1] = "0" + tempDateString2[1];
                            }
                            if (tempDateString2[0].length() < 2) {
                                tempDateString2[0] = "0" + tempDateString2[0];
                            }
                            startDateString = tempDateString2[2] + "-" + tempDateString2[0] + "-" + tempDateString2[1];
                            //System.out.println("startDateString: " + startDateString);
                        }
                        if (items[13].length() > 0) {
                            tempDateString1 = items[13];
                            tempDateString2 = tempDateString1.split("-");
                            if (tempDateString2[1].length() < 2) {
                                tempDateString2[1] = "0" + tempDateString2[1];
                            }
                            if (tempDateString2[0].length() < 2) {
                                tempDateString2[0] = "0" + tempDateString2[0];
                            }
                            endDateString = tempDateString2[2] + "-" + tempDateString2[0] + "-" + tempDateString2[1];
                            //System.out.println("endDateString: " + endDateString);
                        }


                        if (doctorPatientCount < 1) {
                            //System.out.println("Worker person is not there, so add it: ");
                            queryString = "INSERT INTO patient_doctors\n"
                                    + "(patient_id,\n"
                                    + "doctor_id,\n"
                                    + "start_timestamp,\n"
                                    + "end_timestamp)"
                                    + " VALUES \n"
                                    + " (?,?,?,?) "
                                    + " ;  \n"
                            ;
                            try {
                                PreparedStatement ps1 = conn.prepareStatement(queryString);
                                ps1.setInt(1, patientId);
                                ps1.setInt(2, doctorId);
                                ps1.setString(3, startDateString);
                                ps1.setString(4, endDateString);
                                ps1.executeUpdate();
                                ps1.close();
                            } catch (SQLException e) {
                                System.out.println("SQL Exception - Insert worker: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                            }//end try
                        } else {  // if doctor patient record already exists
                            //System.out.println("Doctor Patient relationship is there, updating dates");
                            try {
                                // if Doctor is not in the table, add the doctor
                                queryString = "UPDATE patient_doctors \n"
                                        + " SET end_timestamp = ? "
                                        + " WHERE patient_id = ? "
                                        + " AND doctor_id = ? "
                                        + " ;  \n"
                                ;
                                PreparedStatement ps11 = conn.prepareStatement(queryString);
                                ps11 = conn.prepareStatement(queryString);
                                ps11.setString(1, endDateString);
                                ps11.setInt(2, patientId);
                                ps11.setInt(3, doctorId);
                                ps11.executeUpdate();
                            } catch (SQLException e) {
                                System.out.println("SQL doctor - patient record Exception: " + e.getMessage());
                            } // end try - enter missing doctor - patient relationship info
                        }

                        // add patient room, if there in record
                        if (items[5].length() > 0) {
                            queryString = "REPLACE INTO patient_rooms (patient_id, room_id, start_timestamp, end_timestamp, admitting_physician_id) \n"
                                    + " VALUES \n"
                                    + " (?,?,?,?,?) "
                                    + " ;  \n"
                            ;
                            try {
                                PreparedStatement ps12 = conn.prepareStatement(queryString);
                                // if a patient room is there, add it
                                ps12.setInt(1, patientId);
                                ps12.setInt(2, Integer.parseInt(items[5]));
                                ps12.setString(3, startDateString);
                                ps12.setString(4, endDateString);
                                ps12.setInt(5, doctorId);
                                ps12.executeUpdate();
                                ps12.close();
                            } catch (SQLException e) {
                                System.out.println("SQL inpatient patient room Exception: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                            }
                            //System.out.println("queryString: " + queryString);
                        } // end if -- add patient room # if it is in the record

                        // add diagnosis/treatment, if there
                        int treatmentId = 0;
                        int treatmentCount = 0;
                        int serviceId = 0;
                        int serviceCount = 0;
                        if (items[11].length() > 0) {
                            try {
                                //see if treatment record is there or not
                                // execute statement and pull back data
                                stmt = conn.createStatement();
                                queryString = "SELECT COUNT(*) as treatmentCount FROM treatments  \n"
                                        + " WHERE associated_diagnosis='" + items[11] + "' \n"
                                ;
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    treatmentCount = rs.getInt("treatmentCount");
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL treatmentCount Exception: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                            }
                            // if treatment not in table, add it
                            if (treatmentCount < 1) {
                                //System.out.println("Treatments");
                                queryString = "INSERT INTO treatments  \n"
                                        + " (treatment_short_description, associated_diagnosis)  \n"
                                        + " VALUES  \n"
                                        + " (? || ' Treatment', ? );  \n"
                                ;
                                try {
                                    PreparedStatement ps13 = conn.prepareStatement(queryString);
                                    ps13.setString(1, items[11]);
                                    ps13.setString(2, items[11]);
                                    treatmentId = ps13.executeUpdate();
                                    ps13.close();
                                } catch (SQLException e) {
                                    System.out.println("SQL add treatment Exception: " + e.getMessage());
                                    //System.out.println("queryString: " + queryString);
                                }
                                //System.out.println("queryString: " + queryString);
                            } // end if - add treatment if not there

                            // see if service record is there or not
                            try {
                                //see if service record is there or not
                                // execute statement and pull back data
                                stmt = conn.createStatement();
                                queryString = "SELECT COUNT(*) as serviceCount FROM services  \n"
                                        + " WHERE service_short_description= 'Fix " + items[11] + "' \n"
                                ;
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    serviceCount = rs.getInt("serviceCount");
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL serviceCount Exception: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                            }
                            // if service not in table, add it
                            if (serviceCount < 1) {
                                //System.out.println("Services");
                                queryString = "INSERT INTO services  \n"
                                        + " (service_type, service_short_description)  \n"
                                        + " VALUES  \n"
                                        + " ('inpatient', 'Fix ' || ? );       \n"
                                ;
                                try {
                                    PreparedStatement ps14 = conn.prepareStatement(queryString);
                                    ps14.setString(1, items[11]);
                                    serviceId = ps14.executeUpdate();
                                    ps14.close();
                                } catch (SQLException e) {
                                    System.out.println("SQL add service Exception: " + e.getMessage());
                                    //System.out.println("queryString: " + queryString);
                                }
                                //System.out.println("queryString: " + queryString);
                            } // end if - add service if not there

                            //get values, now that everything is entered into the DB
                            try {
                                //see if treatment record is there or not
                                // execute statement and pull back data
                                stmt = conn.createStatement();
                                queryString = "SELECT max(treatment_id) as treatmentId FROM treatments  \n"
                                        + " WHERE associated_diagnosis='" + items[11] + "' \n"
                                ;
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    treatmentId = rs.getInt("treatmentId");
                                    //System.out.println("treatmentId: " + treatmentId);
                                    //System.out.println("queryString: " + queryString);
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL treatmentId Exception: " + e.getMessage());
                            }
                            //System.out.println("queryString: " + queryString);
                            try {
                                //see if service record is there or not
                                // execute statement and pull back data
                                stmt = conn.createStatement();
                                queryString = "SELECT max(service_id) as serviceId FROM services  \n"
                                        + " WHERE service_short_description= 'Fix " + items[11] + "' \n"
                                ;
                                //System.out.println(queryString);
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    serviceId = rs.getInt("serviceId");
                                    //System.out.println("serviceId: " + serviceId);
                                    //System.out.println("queryString: " + queryString);
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL serviceId Exception: " + e.getMessage());
                            }


                            //add in treatment_component
                            int treatmentComponentId = 0;
                            int treatmentComponentCount = 0;
                            // see if service record is there or not
                            try {
                                //see if service record is there or not
                                // execute statement and pull back data
                                stmt = conn.createStatement();
                                queryString = "SELECT COUNT(*) as treatmentComponentCount FROM treatment_components  \n"
                                        + " WHERE service_id='" + serviceId + "' \n"
                                        + "   AND treatment_id='" + treatmentId + "' \n"
                                ;
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    treatmentComponentCount = rs.getInt("treatmentComponentCount");
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL treatmentComponentCount Exception: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                            }
                            // if treatmentComponent not in table, add it
                            if (treatmentComponentCount < 1) {
                                //System.out.println("treatmentComponents");
                                queryString = "INSERT INTO treatment_components   \n"
                                        + " (treatment_id, service_id)   \n"
                                        + " VALUES   \n"
                                        + " (?,? ) ; \n"
                                ;
                                try {
                                    PreparedStatement ps14 = conn.prepareStatement(queryString);
                                    ps14.setInt(1, treatmentId);
                                    ps14.setInt(2, serviceId);
                                    treatmentComponentId = ps14.executeUpdate();
                                    ps14.close();
                                    //System.out.println("queryString: " + queryString);
                                } catch (SQLException e) {
                                    System.out.println("SQL treatment component Exception: " + e.getMessage());
                                }
                                //System.out.println("queryString: " + queryString);
                            } // end if - add treatmentComponentCount if not there
                            try {
                                stmt = conn.createStatement();
                                queryString = "SELECT max(treatment_component_id) as treatmentComponentId FROM treatment_components  \n"
                                        + " WHERE service_id='" + serviceId + "' \n"
                                        + "   AND treatment_id='" + treatmentId + "' \n"
                                ;
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    treatmentComponentId = rs.getInt("treatmentComponentId");
                                    //System.out.println("treatmentComponentId: " + treatmentComponentId);
                                    //System.out.println("queryString: " + queryString);
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL treatmentComponentId Exception: " + e.getMessage());
                            }


                            //add in patient_treatment_component
                            int patientTreatmentComponentId = 0;
                            int patientTreatmentComponentCount = 0;
                            // see if service record is there or not
                            try {
                                //see if service record is there or not
                                // execute statement and pull back data
                                stmt = conn.createStatement();
                                queryString = "SELECT COUNT(*) as patientTreatmentComponentCount FROM patient_treatment_components  \n"
                                        + " WHERE patient_id='" + patientId + "' \n"
                                        + "   AND treatment_component_id='" + treatmentComponentId + "' \n"
                                        + "   AND ordering_physician_id='" + doctorId + "' \n"
                                        + "   AND diagnosis_timestamp='" + startDateString + "' \n"
                                        + "   AND treatment_start_timestamp='" + startDateString + "' \n"
                                        + "   AND initial_diagnosis='Y' \n"
                                ;
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    patientTreatmentComponentCount = rs.getInt("patientTreatmentComponentCount");
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL patientTreatmentComponentCount Exception: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                            }
                            // if patientTreatmentComponentCount not in table, add it
                            if (patientTreatmentComponentCount < 1) {
                                //System.out.println("treatmentComponents");
                                queryString = "INSERT INTO patient_treatment_components\n" +
                                        "(\n" +
                                        "patient_id,\n" +
                                        "diagnosis_description,\n" +
                                        "initial_diagnosis,\n" +
                                        "diagnosis_timestamp,\n" +
                                        "treatment_start_timestamp,\n" +
                                        "treatment_end_timestamp,\n" +
                                        "treatment_component_id,\n" +
                                        "ordering_physician_id)"
                                        + " VALUES   \n"
                                        + " (?,?,?,?,?,?,?,?) ; \n"
                                ;
                                try {
                                    PreparedStatement ps14 = conn.prepareStatement(queryString);
                                    ps14.setInt(1, patientId);
                                    ps14.setString(2, items[11]);
                                    ps14.setString(3, "Y");
                                    ps14.setString(4, startDateString);
                                    ps14.setString(5, startDateString);
                                    ps14.setString(6, endDateString);
                                    ps14.setInt(7, treatmentComponentId);
                                    ps14.setInt(8, doctorId);
                                    patientTreatmentComponentId = ps14.executeUpdate();
                                    ps14.close();
                                    //System.out.println("queryString: " + queryString);
                                } catch (SQLException e) {
                                    System.out.println("SQL patient treatment component Exception: " + e.getMessage());
                                }
                                //System.out.println("queryString: " + queryString);
                            } // end if - add patientTreatmentComponentId if not there
                            try {
                                stmt = conn.createStatement();
                                queryString = "SELECT MAX(patient_treatment_component_id) as patientTreatmentComponentId FROM patient_treatment_components  \n"
                                        + " WHERE patient_id='" + patientId + "' \n"
                                        + "   AND treatment_component_id='" + treatmentComponentId + "' \n"
                                        + "   AND ordering_physician_id='" + doctorId + "' \n"
                                        + "   AND diagnosis_timestamp='" + startDateString + "' \n"
                                        + "   AND treatment_start_timestamp='" + startDateString + "' \n"
                                        + "   AND initial_diagnosis='Y' \n"
                                ;
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    patientTreatmentComponentId = rs.getInt("patientTreatmentComponentId");
                                    //System.out.println("treatmentComponentId: " + treatmentComponentId);
                                    //System.out.println("queryString: " + queryString);
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL patientTreatmentComponentId Exception: " + e.getMessage());
                            }

                            //add in patient_treatment_administered
                            int patientTreatmentAdministeredId = 0;
                            int patientTreatmentAdministeredCount = 0;
                            // see if service record is there or not
                            try {
                                //see if service record is there or not
                                // execute statement and pull back data
                                stmt = conn.createStatement();
                                queryString = "SELECT COUNT(*) as patientTreatmentAdministeredCount FROM patient_treatment_administered  \n"
                                        + " WHERE patient_treatment_component_id='" + patientTreatmentComponentId + "' \n"
                                        + "   AND worker_id='" + doctorId + "' \n"
                                        + "   AND adminstered_time_stamp='" + startDateString + "' \n"
                                ;
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    patientTreatmentAdministeredCount = rs.getInt("patientTreatmentAdministeredCount");
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL patientTreatmentAdministeredCount Exception: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                            }
                            // if patientTreatmentAdministeredCount not in table, add it


                            //System.out.println("patientTreatmentComponentId: " + patientTreatmentComponentId);
                            //System.out.println("doctorId: " + doctorId);
                            //System.out.println("startDateString: " + startDateString);
                            if (patientTreatmentAdministeredCount < 1) {
                                //System.out.println("treatmentComponents");
                                queryString = "INSERT INTO patient_treatment_administered\n" +
                                        "(\n" +
                                        "patient_treatment_component_id,\n" +
                                        "worker_id,\n" +
                                        "adminstered_time_stamp)"
                                        + " VALUES   \n"
                                        + " (?,?,?) ; \n"
                                ;
                                //System.out.println("queryString: " + queryString);
                                try {
                                    PreparedStatement ps14 = conn.prepareStatement(queryString);
                                    ps14.setInt(1, patientTreatmentComponentId);
                                    ps14.setInt(2, doctorId);
                                    ps14.setString(3, startDateString);
                                    patientTreatmentAdministeredId = ps14.executeUpdate();
                                    ps14.close();
                                    //System.out.println("queryString: " + queryString);
                                } catch (SQLException e) {
                                    System.out.println("SQL patient treatment administered Exception: " + e.getMessage());
                                    e.printStackTrace();
                                }
                                //System.out.println("queryString: " + queryString);
                            } // end if - add patientTreatmentComponentId if not there
                            try {
                                stmt = conn.createStatement();
                                queryString = "SELECT MAX(patient_treatment_administered_id) as patientTreatmentAdministeredId FROM patient_treatment_administered  \n"
                                        + " WHERE patient_treatment_component_id='" + patientTreatmentComponentId + "' \n"
                                        + "   AND worker_id='" + doctorId + "' \n"
                                        + "   AND adminstered_time_stamp='" + startDateString + "' \n"
                                ;
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    patientTreatmentAdministeredId = rs.getInt("patientTreatmentAdministeredId");
                                    //System.out.println("treatmentComponentId: " + treatmentComponentId);
                                    //System.out.println("queryString: " + queryString);
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL patientTreatmentAdministeredId Exception: " + e.getMessage());
                            }

                        }//end if initial diagnosis exists

                    }//end if patient insert/update

                } // if the data line which was read-in has data in it

            } // end while - read each line of the file

            //System.out.println("People inserted: " + counter);
            //System.out.println("People attempted: " + attemptCounter);

        } catch (Exception e) {
            System.out.println("Exception importPeopleData: " + e.getMessage());
            e.printStackTrace();
        }
        //System.out.println("queryString: " + queryString);
    } //end importPeopleData


    //begin importTreatmentData
    public void importTreatmentData(String importTreatmentDataFilename) throws Exception {
        //1. Get the data from the file
        int personCounter = 0;
        int patientId = 0;
        int doctorId = 0;

        int medicationId = 0;
        int medicationCount = 0;
        //1. Get the data from the file
        String input = "";
        String defaultString = "";
        int counter = 0;
        int attemptCounter = 0;
        String previousString = "";
        String queryString = "";
        Statement stmt = null;
        String[] items = new String[5];
        int treatmentId = 0;
        int treatmentCount = 0;
        int serviceId = 0;
        int serviceCount = 0;

        try {
            Scanner s = new Scanner(new File(importTreatmentDataFilename));

            while (s.hasNextLine()) {
                // go get another line of data
                input = s.nextLine();
                if (input != null) {
                    // System.out.println(input);
                    items = input.split(",", -1);
                    attemptCounter++;
                    //check that first field is valid
                    //System.out.println(items[0]);

                    //1. Insert into import person table queryString
                    queryString = "INSERT INTO import_treatment_data (patient_last_name, ordering_physician_last_name, treatment_type, short_description, timestamp) \n"
                            + " VALUES \n"
                            + " (?,?,?,?,?);";

                    Connection conn = this.connect();
                    try {
                        PreparedStatement ps = conn.prepareStatement(queryString);
                        for (int j = 1; j < 6; j++) {
                            ps.setString(j, items[j - 1]);
                        }
                        ps.executeUpdate();
                        ps.close();
                        counter++;

                    } catch (SQLException e) {
                        System.out.println("SQL import_person_data Exception: " + e.getMessage());
                        //System.out.println("queryString: " + queryString);
                    }
                    //System.out.println("queryString: " + queryString);

                    //2. Put the data from the file into the correct tables
                    personCounter = 0;
                    if (items[2].equals("M")
                            || items[2].equals("P")
                    ) {
                        // date manipulation
                        String startDateString = "", tempDateString1 = "", tempDateString2[] = null, tempDateString3[] = null;
                        if (items[4].length() > 0) {
                            //date part
                            tempDateString1 = items[4];
                            tempDateString2 = tempDateString1.split("-");
                            //day
                            if (tempDateString2[1].length() < 2) {
                                tempDateString2[1] = "0" + tempDateString2[1];
                            }
                            //month
                            if (tempDateString2[0].length() < 2) {
                                tempDateString2[0] = "0" + tempDateString2[0];
                            }
                            //year
                            if (tempDateString2[2].length() > 4) {
                                tempDateString2[2] = tempDateString2[2].substring(0, 4);
                            }
                            //put date together
                            startDateString = tempDateString2[2] + "-" + tempDateString2[0] + "-" + tempDateString2[1];
                            //System.out.println("startDateString: " + startDateString);

                            //time part
                            tempDateString3 = tempDateString1.split(" ");
                            // if second part holds a time string, use it
                            if (tempDateString3[1].length() > 0) {
                                // already in HH:MM format https://www.sqlite.org/lang_datefunc.html
                                //add time part
                                startDateString += "T";
                                startDateString += tempDateString3[1];
                            }
                        }
                        //System.out.println("startDateString: " + startDateString);

                        //System.out.println("see if patient is there or not: ");
                        try {
                            // execute statement and pull back data
                            //System.out.println("index 2: " + items[2]);
                            stmt = conn.createStatement();
                            queryString = "SELECT COUNT(*) as personCounter FROM patients WHERE lower(patient_last_name)=lower('" + items[0] + "') \n";
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                personCounter = rs.getInt("personCounter");
                                //System.out.println("personCounter: " + personCounter);
                            }
                            stmt.close();
                        } catch (SQLException e) {
                            System.out.println("SQL Exception - personCounter: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                            //System.out.println("patient_last_name: " + items[0]);
                            //System.out.println("personCounter: " + personCounter);
                        }
                        //if person is not there, add it
                        if (personCounter < 1) {
                            //System.out.println("Patient is not there, so add it: ");
                            queryString = "INSERT INTO patients (patient_id, patient_last_name, primary_physician_last_name) \n"
                                    + " VALUES \n"
                                    + " (?,?,?) "
                                    + " ;  \n"
                            ;
                            try {
                                PreparedStatement ps7 = conn.prepareStatement(queryString);
                                ps7.setNull(1, Types.NULL);
                                ps7.setString(2, items[0]);
                                ps7.setString(3, items[1]);
                                patientId = ps7.executeUpdate();
                                ps7.close();
                            } catch (SQLException e) {
                                System.out.println("SQL Exception - insert patient: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                                //System.out.println("patient_last_name: " + items[0]);
                                //System.out.println("doctor_last_name: " + items[1]);
                            }

                        }
                        //get patientId
                        try {
                            // execute statement and pull back data
                            //System.out.println("index 2: " + items[2]);
                            stmt = conn.createStatement();
                            queryString = "SELECT MAX(patient_id) AS patientId FROM patients WHERE lower(patient_last_name)=lower('" + items[0] + "') \n";
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                patientId = rs.getInt("patientId");
                                //System.out.println("personCounter: " + personCounter);
                            }
                            stmt.close();
                        } catch (SQLException e) {
                            System.out.println("SQL Exception - personCounter: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                            //System.out.println("patient_last_name: " + items[2]);
                            //System.out.println("personCounter: " + personCounter);
                        }

                        //check if doctor listed on patient record is in workers table
                        personCounter = 0;
                        //Volunteer, Doctor, Nurse, Technician, Administrator
                        try {
                            // execute statement and pull back data
                            //System.out.println("index 2: " + items[2]);
                            stmt = conn.createStatement();
                            queryString = "SELECT COUNT(*) as personCounter FROM workers WHERE lower(worker_last_name)=lower('" + items[1] + "') \n";
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                personCounter = rs.getInt("personCounter");
                                //System.out.println("personCounter: " + personCounter);
                            }
                            stmt.close();
                        } catch (SQLException e) {
                            System.out.println("SQL Exception - personCounter: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                            //System.out.println("worker_last_name: " + items[2]);
                            //System.out.println("personCounter: " + personCounter);
                        }
                        //System.out.println("Worker person data: ");
                        //for (int i=0; i<items.length; i++){
                        //    System.out.println("items["+i+"]: " + items[i]);
                        //}
                        //if person is not there, add it
                        if (personCounter < 1) {
                            //System.out.println("Worker person is not there, so add it: ");
                            queryString = "INSERT INTO workers (worker_last_name, worker_type_id)  \n"
                                    + " VALUES \n"
                                    + " (?,(SELECT worker_type_id FROM worker_types WHERE worker_type_code='D'));";
                            try {
                                PreparedStatement ps1 = conn.prepareStatement(queryString);
                                ps1.setString(1, items[1]);
                                ps1.executeUpdate();
                                ps1.close();
                            } catch (SQLException e) {
                                System.out.println("SQL Exception - Insert worker: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                            }//end try
                        }

                        //get doctorId
                        try {
                            // execute statement and pull back data
                            //System.out.println("index 2: " + items[2]);
                            stmt = conn.createStatement();
                            queryString = "SELECT MAX(worker_id) AS doctorId FROM workers WHERE lower(worker_last_name)=lower('" + items[1] + "') \n";
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                doctorId = rs.getInt("doctorId");
                                //System.out.println("personCounter: " + personCounter);
                            }
                            stmt.close();
                        } catch (SQLException e) {
                            System.out.println("SQL Exception - personCounter: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                            //System.out.println("worker_last_name: " + items[2]);
                            //System.out.println("personCounter: " + personCounter);
                        }


                        // medicine or service
                        if (items[2].equals("M")) {
                            // see if the medicine already exists
                            try {
                                // execute statement and pull back data
                                stmt = conn.createStatement();
                                queryString = "SELECT COUNT(*) as medicationCount FROM medications WHERE lower(medication_description)=lower('" + items[3] + "') \n";
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    medicationCount = rs.getInt("medicationCount");
                                }
                                stmt.close();
                            } catch (SQLException e) {
                                System.out.println("SQL Exception - medicationCount: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                                //System.out.println("medication_description: " + items[3]);
                            }
                            //if medication is not there, add it
                            if (medicationCount < 1) {
                                //System.out.println("Medication is not there, so add it: ");
                                queryString = "INSERT INTO medications (medication_id, medication_description, medication_dosage, medication_frequency) \n"
                                        + " VALUES \n"
                                        + " (?,?, 'Standard','Daily') "
                                        + " ;  \n"
                                ;
                                try {
                                    PreparedStatement ps7 = conn.prepareStatement(queryString);
                                    ps7.setNull(1, Types.NULL);
                                    ps7.setString(2, items[3]);
                                    medicationId = ps7.executeUpdate();
                                    ps7.close();
                                } catch (SQLException e) {
                                    System.out.println("SQL Exception - insert medication: " + e.getMessage());
                                    //System.out.println("medication_description: " + items[3]);
                                }

                            }
                            //if medication is there, get id
                            try {
                                // execute statement and pull back data
                                stmt = conn.createStatement();
                                queryString = "SELECT MAX(medication_id) AS medicationId FROM medications WHERE lower(medication_description)=lower('" + items[3] + "') \n";
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    medicationId = rs.getInt("medicationId");
                                }
                                stmt.close();
                            } catch (SQLException e) {
                                System.out.println("SQL Exception - medicationId retrieval: " + e.getMessage());
                                //System.out.println("medication_description: " + items[3]);
                            }
                        }
                        if (items[2].equals("P")) {
                            // see if service record is there or not
                            try {
                                //see if service record is there or not
                                stmt = conn.createStatement();
                                queryString = "SELECT COUNT(*) as serviceCount FROM services  \n"
                                        + " WHERE lower(service_short_description)= lower('" + items[3] + "') \n"
                                ;
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    serviceCount = rs.getInt("serviceCount");
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL serviceCount Exception: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                            }
                            // if service not in table, add it
                            // NOTE: assume everything is outpatient, unless the data is part of a patient import file
                            if (serviceCount < 1) {
                                //System.out.println("Services");
                                queryString = "INSERT INTO services  \n"
                                        + " (service_type, service_short_description)  \n"
                                        + " VALUES  \n"
                                        + " ('outpatient', ? );       \n"
                                ;
                                try {
                                    PreparedStatement ps14 = conn.prepareStatement(queryString);
                                    ps14.setString(1, items[3]);
                                    serviceId = ps14.executeUpdate();
                                    ps14.close();
                                } catch (SQLException e) {
                                    System.out.println("SQL add service Exception: " + e.getMessage());
                                    //System.out.println("queryString: " + queryString);
                                }
                                //System.out.println("queryString: " + queryString);
                            } // end if - add service if not there
                            try {
                                //see if service record is there or not
                                // execute statement and pull back data
                                stmt = conn.createStatement();
                                queryString = "SELECT max(service_id) as serviceId FROM services  \n"
                                        + " WHERE lower(service_short_description)= lower('" + items[3] + "') \n"
                                ;
                                //System.out.println(queryString);
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    serviceId = rs.getInt("serviceId");
                                    //System.out.println("serviceId: " + serviceId);
                                    //System.out.println("queryString: " + queryString);
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL serviceId Exception: " + e.getMessage());
                            }
                        }


                        // add diagnosis/treatment, if there
                        try {
                            //see if treatment record is there or not
                            // execute statement and pull back data
                            stmt = conn.createStatement();
                            queryString = "SELECT COUNT(*) as treatmentCount FROM treatments  \n"
                                    + " WHERE treatment_short_description='" + items[3] + " Treatment' \n"
                            ;
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                treatmentCount = rs.getInt("treatmentCount");
                            }
                        } catch (SQLException e) {
                            System.out.println("SQL treatmentCount Exception: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                        }
                        // if treatment not in table, add it
                        if (treatmentCount < 1) {
                            //System.out.println("Treatments");
                            queryString = "INSERT INTO treatments  \n"
                                    + " (treatment_short_description, associated_diagnosis)  \n"
                                    + " VALUES  \n"
                                    + " (? || ' Treatment', ? || ' required');  \n"
                            ;
                            try {
                                PreparedStatement ps13 = conn.prepareStatement(queryString);
                                ps13.setString(1, items[3]);
                                ps13.setString(2, items[3]);
                                treatmentId = ps13.executeUpdate();
                                ps13.close();
                            } catch (SQLException e) {
                                System.out.println("SQL add treatment Exception: " + e.getMessage());
                                //System.out.println("queryString: " + queryString);
                            }
                            //System.out.println("queryString: " + queryString);
                        } // end if - add treatment if not there

                        //get values, now that everything is entered into the DB
                        try {
                            //see if treatment record is there or not
                            // execute statement and pull back data
                            stmt = conn.createStatement();
                            queryString = "SELECT max(treatment_id) as treatmentId FROM treatments  \n"
                                    + " WHERE treatment_short_description='" + items[3] + " Treatment' \n"
                            ;
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                treatmentId = rs.getInt("treatmentId");
                                //System.out.println("treatmentId: " + treatmentId);
                                //System.out.println("queryString: " + queryString);
                            }
                        } catch (SQLException e) {
                            System.out.println("SQL treatmentId Exception: " + e.getMessage());
                        }
                        //System.out.println("queryString: " + queryString);

                        //add in treatment_component
                        int treatmentComponentId = 0;
                        int treatmentComponentCount = 0;
                        // see if treatment_components record is there or not

                        if (items[2].equals("M")) {
                            queryString = "SELECT COUNT(*) as treatmentComponentCount FROM treatment_components  \n"
                                    + " WHERE medication_id='" + medicationId + "' \n"
                                    + "   AND treatment_id='" + treatmentId + "' \n"
                            ;

                        }
                        if (items[2].equals("P")) {
                            queryString = "SELECT COUNT(*) as treatmentComponentCount FROM treatment_components  \n"
                                    + " WHERE service_id='" + serviceId + "' \n"
                                    + "   AND treatment_id='" + treatmentId + "' \n"
                            ;

                        }
                        try {
                            //see if service record is there or not
                            // execute statement and pull back data
                            stmt = conn.createStatement();
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                treatmentComponentCount = rs.getInt("treatmentComponentCount");
                            }
                        } catch (SQLException e) {
                            System.out.println("SQL treatmentComponentCount Exception: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                        }


                        // if treatmentComponent not in table, add it
                        if (items[2].equals("M")) {
                            if (treatmentComponentCount < 1) {
                                //System.out.println("treatmentComponents");
                                queryString = "INSERT INTO treatment_components   \n"
                                        + " (treatment_id, medication_id)   \n"
                                        + " VALUES   \n"
                                        + " (?,? ) ; \n"
                                ;
                                try {
                                    PreparedStatement ps14 = conn.prepareStatement(queryString);
                                    ps14.setInt(1, treatmentId);
                                    ps14.setInt(2, medicationId);
                                    treatmentComponentId = ps14.executeUpdate();
                                    ps14.close();
                                    //System.out.println("queryString: " + queryString);
                                } catch (SQLException e) {
                                    System.out.println("SQL treatment component medication Exception: " + e.getMessage());
                                }
                                //System.out.println("queryString: " + queryString);
                            } // end if - add treatmentComponentCount if not there
                            try {
                                stmt = conn.createStatement();
                                queryString = "SELECT max(treatment_component_id) as treatmentComponentId FROM treatment_components  \n"
                                        + " WHERE medication_id='" + medicationId + "' \n"
                                        + "   AND treatment_id='" + treatmentId + "' \n"
                                ;
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    treatmentComponentId = rs.getInt("treatmentComponentId");
                                    //System.out.println("Assigned treatmentComponentId: " + treatmentComponentId);
                                    //System.out.println("queryString: " + queryString);
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL treatmentComponentId medication_id Exception: " + e.getMessage());
                            }

                        }
                        // if treatmentComponent not in table, add it
                        if (items[2].equals("P")) {
                            if (treatmentComponentCount < 1) {
                                //System.out.println("treatmentComponents");
                                queryString = "INSERT INTO treatment_components   \n"
                                        + " (treatment_id, service_id)   \n"
                                        + " VALUES   \n"
                                        + " (?,? ) ; \n"
                                ;
                                try {
                                    PreparedStatement ps14 = conn.prepareStatement(queryString);
                                    ps14.setInt(1, treatmentId);
                                    ps14.setInt(2, serviceId);
                                    treatmentComponentId = ps14.executeUpdate();
                                    ps14.close();
                                    //System.out.println("queryString: " + queryString);
                                } catch (SQLException e) {
                                    System.out.println("SQL treatment component Exception: " + e.getMessage());
                                }
                                //System.out.println("queryString: " + queryString);
                            } // end if - add treatmentComponentCount if not there
                            try {
                                stmt = conn.createStatement();
                                queryString = "SELECT max(treatment_component_id) as treatmentComponentId FROM treatment_components  \n"
                                        + " WHERE service_id='" + serviceId + "' \n"
                                        + "   AND treatment_id='" + treatmentId + "' \n"
                                ;
                                ResultSet rs = stmt.executeQuery(queryString);
                                // loop through the result set
                                while (rs.next()) {
                                    treatmentComponentId = rs.getInt("treatmentComponentId");
                                    //System.out.println("Assigned treatmentComponentId: " + treatmentComponentId);
                                    //System.out.println("queryString: " + queryString);
                                }
                            } catch (SQLException e) {
                                System.out.println("SQL treatmentComponentId service Exception: " + e.getMessage());
                            }

                        }
                        //System.out.println("treatmentComponentId: " + treatmentComponentId);
                        //System.out.println("treatmentId: " + treatmentId);
                        //System.out.println("serviceId: " + serviceId);
                        //System.out.println("medicationId: " + medicationId);
                        //System.out.println("patientId: " + patientId);
                        //System.out.println("doctorId: " + doctorId);
                        //System.out.println("m/p: " + items[3]);


                        //add in patient_treatment_component
                        int patientTreatmentComponentId = 0;
                        int patientTreatmentComponentCount = 0;
                        // see if service record is there or not
                        try {
                            //see if service record is there or not
                            // assuming never an initial diagnosis (those are only associated with a patient import record)
                            stmt = conn.createStatement();
                            queryString = "SELECT COUNT(*) as patientTreatmentComponentCount FROM patient_treatment_components  \n"
                                    + " WHERE patient_id='" + patientId + "' \n"
                                    + "   AND diagnosis_description='" + items[3] + "'\n"
                                    + "   AND treatment_component_id='" + treatmentComponentId + "' \n"
                                    + "   AND ordering_physician_id='" + doctorId + "' \n"
                                    + "   AND diagnosis_timestamp='" + startDateString + "' \n"
                                    + "   AND treatment_start_timestamp='" + startDateString + "' \n"
                                    + "   AND initial_diagnosis='N' \n"
                            ;
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                patientTreatmentComponentCount = rs.getInt("patientTreatmentComponentCount");
                            }
                        } catch (SQLException e) {
                            System.out.println("SQL patientTreatmentComponentCount Exception: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                        }
                        // if patientTreatmentComponentCount not in table, add it
                        if (patientTreatmentComponentCount < 1) {
                            //System.out.println("treatmentComponents");
                            //assume outpatient
                            queryString = "INSERT INTO patient_treatment_components\n" +
                                    "(\n" +
                                    "patient_id,\n" +
                                    "diagnosis_description,\n" +
                                    "initial_diagnosis,\n" +
                                    "treatment_start_timestamp,\n" +
                                    "treatment_component_id,\n" +
                                    "ordering_physician_id,\n" +
                                    "diagnosis_timestamp)"
                                    + " VALUES   \n"
                                    + " (?,?,?,?,?,?,?) ; \n"
                            ;
                            try {
                                PreparedStatement ps14 = conn.prepareStatement(queryString);
                                ps14.setInt(1, patientId);
                                ps14.setString(2, items[3]);
                                ps14.setString(3, "N");
                                ps14.setString(4, startDateString);
                                ps14.setInt(5, treatmentComponentId);
                                ps14.setInt(6, doctorId);
                                ps14.setString(7, startDateString);
                                patientTreatmentComponentId = ps14.executeUpdate();
                                ps14.close();
                                //System.out.println("queryString: " + queryString);
                            } catch (SQLException e) {
                                System.out.println("SQL patient treatment component Exception: " + e.getMessage());
                            }
                            //System.out.println("queryString: " + queryString);
                        } // end if - add patientTreatmentComponentId if not there
                        try {
                            stmt = conn.createStatement();
                            queryString = "SELECT MAX(patient_treatment_component_id) as patientTreatmentComponentId FROM patient_treatment_components  \n"
                                    + " WHERE patient_id='" + patientId + "' \n"
                                    + "   AND diagnosis_description='" + items[3] + "'\n"
                                    + "   AND treatment_component_id='" + treatmentComponentId + "' \n"
                                    + "   AND ordering_physician_id='" + doctorId + "' \n"
                                    + "   AND diagnosis_timestamp='" + startDateString + "' \n"
                                    + "   AND treatment_start_timestamp='" + startDateString + "' \n"
                                    + "   AND initial_diagnosis='N' \n"
                            ;
                            //System.out.println("queryString: " + queryString);
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                patientTreatmentComponentId = rs.getInt("patientTreatmentComponentId");
                                //System.out.println("treatmentComponentId: " + treatmentComponentId);
                                //System.out.println("queryString: " + queryString);
                            }
                        } catch (SQLException e) {
                            System.out.println("SQL patientTreatmentComponentId Exception: " + e.getMessage());
                        }

                        //add in patient_treatment_administered
                        int patientTreatmentAdministeredId = 0;
                        int patientTreatmentAdministeredCount = 0;
                        // see if service record is there or not
                        try {
                            //see if service record is there or not
                            // execute statement and pull back data
                            stmt = conn.createStatement();
                            queryString = "SELECT COUNT(*) as patientTreatmentAdministeredCount FROM patient_treatment_administered  \n"
                                    + " WHERE patient_treatment_component_id='" + patientTreatmentComponentId + "' \n"
                                    + "   AND worker_id='" + doctorId + "' \n"
                                    + "   AND adminstered_time_stamp='" + startDateString + "' \n"
                            ;
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                patientTreatmentAdministeredCount = rs.getInt("patientTreatmentAdministeredCount");
                            }
                        } catch (SQLException e) {
                            System.out.println("SQL patientTreatmentAdministeredCount Exception: " + e.getMessage());
                            //System.out.println("queryString: " + queryString);
                        }
                        // if patientTreatmentAdministeredCount not in table, add it

                        //System.out.println("m/p: " + items[3]);
                        //System.out.println("patientTreatmentComponentId: " + patientTreatmentComponentId);
                        //System.out.println("doctorId: " + doctorId);
                        //System.out.println("startDateString: " + startDateString);
                        if (patientTreatmentAdministeredCount < 1) {
                            //System.out.println("treatmentComponents");
                            queryString = "INSERT INTO patient_treatment_administered\n" +
                                    "(\n" +
                                    "patient_treatment_component_id,\n" +
                                    "worker_id,\n" +
                                    "adminstered_time_stamp)"
                                    + " VALUES   \n"
                                    + " (?,?,?) ; \n"
                            ;
                            //System.out.println("queryString: " + queryString);
                            try {
                                PreparedStatement ps14 = conn.prepareStatement(queryString);
                                ps14.setInt(1, patientTreatmentComponentId);
                                ps14.setInt(2, doctorId);
                                ps14.setString(3, startDateString);
                                patientTreatmentAdministeredId = ps14.executeUpdate();
                                ps14.close();
                                //System.out.println("queryString: " + queryString);
                            } catch (SQLException e) {
                                System.out.println("SQL patient treatment administered Exception: " + e.getMessage());
                            }
                            //System.out.println("queryString: " + queryString);
                        } // end if - add patientTreatmentAdministeredId if not there
                        try {
                            stmt = conn.createStatement();
                            queryString = "SELECT MAX(patient_treatment_administered_id) as patientTreatmentAdministeredId FROM patient_treatment_administered  \n"
                                    + " WHERE patient_treatment_component_id='" + patientTreatmentComponentId + "' \n"
                                    + "   AND worker_id='" + doctorId + "' \n"
                                    + "   AND adminstered_time_stamp='" + startDateString + "' \n"
                            ;
                            ResultSet rs = stmt.executeQuery(queryString);
                            // loop through the result set
                            while (rs.next()) {
                                patientTreatmentAdministeredId = rs.getInt("patientTreatmentAdministeredId");
                                //System.out.println("treatmentComponentId: " + treatmentComponentId);
                                //System.out.println("queryString: " + queryString);
                            }
                        } catch (SQLException e) {
                            System.out.println("SQL patientTreatmentAdministeredId Exception: " + e.getMessage());
                        }

                    } // if the data line which was read-in has M/P
                    //System.out.println("queryString: " + queryString);
                } // if the data line which was read-in has data in it (no null)
            } // end while - read each line of the file
        } catch (Exception e) {
            System.out.println("Exception importTreatmentData:" + e.getMessage());
            e.printStackTrace();
        } // end try
        //System.out.println("queryString: " + queryString);
    } //end importTreatmentData


    private Connection connect() throws Exception {
        // SQLite connection string
        String url = DATABASE_STRING;
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection(url);
        try {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                //System.out.println("The driver name is " + meta.getDriverName());
                //System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try {
            conn = DriverManager.getConnection(url);
            //This will turn on foreign keys
            //by default SQLite turns them off
            conn.createStatement().executeUpdate("PRAGMA foreign_keys = ON;");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    /* create db */
    public void createDatabase() {

        populateCreateTableStringArray();
        populatePopulateLookupTables();

        try (Connection conn = DriverManager.getConnection(DATABASE_STRING);
             Statement stmt = conn.createStatement()) {
            // create the tables
            for (int i = 0; i < CREATE_TABLE_STRINGS.length; i++) {
                //System.out.println(CREATE_TABLE_STRINGS[i]);
                stmt.execute(CREATE_TABLE_STRINGS[i]);
            }
            for (int i = 0; i < POPULATE_TABLE_LOOKUPS.length; i++) {
                //System.out.println(CREATE_TABLE_STRINGS[i]);
                stmt.execute(POPULATE_TABLE_LOOKUPS[i]);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }//end try-catch block
    }//end method createDatabase


    /* delete db */
    public void deleteDatabase() {
        String databaseFileName = DATABASE_PATH + DATABASE_NAME;
        int databaseExists = 0;
        try {
            databaseExists = checkFile(databaseFileName);
            if (databaseExists > 0) {
                deleteFile(databaseFileName);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //end try-catch block
    }//end method createDatabase


    /* run query */
    public void runQuery(String queryString) {
        try {
            Connection con = DriverManager.getConnection(DATABASE_STRING);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(queryString);
            ResultSetMetaData rsmd = rs.getMetaData();

            //set up column headers
            for (int i = 1; i < rsmd.getColumnCount(); i++) {
                System.out.print(rsmd.getColumnName(i) + "\t");
            }
            System.out.println("");
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out.print("-------" + "\t");
            }
            System.out.println("");
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println("");

            }
            con.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println(queryString);
        }//end try-catch block
        //System.out.println("queryString: " + queryString);
    }//end method createDatabase

    /* getters and settings for each table */


    /* helper: get primary key name from meta data */
    public String getKeyForTable(String searchedTableName) throws Exception {
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection(DATABASE_STRING);
        DatabaseMetaData dbmeta = conn.getMetaData();

        //housekeeping
        String queryString = "";
        String result = "";

        // Listing Tables
        String catalog = null;
        String schemaPattern = null;
        String tableNamePattern = null;
        String[] types = null;

        ResultSet tables = dbmeta.getTables(catalog, schemaPattern, tableNamePattern, types);
        while (tables.next()) { // for each table
            final String tableName = tables.getString(3);
            if (tableName == searchedTableName) {
                // Listing Columns in a Table
                String columnNamePattern = null;
                ResultSet columns = dbmeta.getColumns(catalog, schemaPattern, tableName, columnNamePattern);
                String schema = null;
                ResultSet keys = dbmeta.getPrimaryKeys(catalog, schema, tableName);
                while (keys.next()) {
                    String keyName = keys.getString(4); // COLUMN_NAME
                    int keySeq = keys.getInt("KEY_SEQ");
                    result = keyName;
                    // System.out.println("primary key: " + keyName + ", " + keySeq);
                }
            }//end if - look up based on table name

        }
        return result;
    }// end getKeyForTable

    public String[] getTableList(String searchedTableName) throws Exception {
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection(DATABASE_STRING);
        DatabaseMetaData dbmeta = conn.getMetaData();

        //housekeeping
        String queryString = "";
        String result = "";

        // Listing Tables
        String catalog = null;
        String schemaPattern = null;
        String tableNamePattern = null;
        String[] types = null;

        int tableCount = 0;
        ResultSet tables = dbmeta.getTables(catalog, schemaPattern, tableNamePattern, types);
        while (tables.next()) {
            tableCount++;
        }

        String[] tableList = new String[tableCount];
        tables.beforeFirst();
        int counter = 0;
        while (tables.next()) {
            tableList[counter] = tables.getString(3);
            ;
            counter++;
        }

        return tableList;
    }// end getTableList


    public void selectSample() throws Exception {
        String queryString = "SELECT treatment_id, treatment_short_description, treatment_description, associated_diagnosis FROM treatments;";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryString)) {

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("treatment_id") + "\t" +
                        rs.getString("treatment_short_description") + "\t");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //System.out.println("queryString: " + queryString);
    }

    public void insertSample(String snum, String sname, int status, String city) throws Exception {

        //This is BAD!  Do NOT use!
        //This allows SQL injection attacks!
        String BADsql = "INSERT INTO Suppliers(snum, sname, status, city) VALUES (" + snum + "," + sname + "," + status + "," + city + ");";

        //This is much better!
        String queryString = "INSERT INTO Suppliers(snum, sname, status, city) VALUES (?,?,?,?);";

        try (Connection conn = this.connect();) {

            PreparedStatement ps = conn.prepareStatement(queryString);
            ps.setString(1, snum); //First ? in sql
            ps.setString(2, sname); //Second ? in sql
            ps.setInt(3, status); //Third ? in sql
            ps.setString(4, city); //Fourth ? in sql
            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //System.out.println("queryString: " + queryString);

    }

    // Database create statement
    String[] CREATE_TABLE_STRINGS = new String[17];

    private void populateCreateTableStringArray() {
        CREATE_TABLE_STRINGS[0] =
                "CREATE TABLE IF NOT EXISTS worker_types (\n"
                        + "   worker_type_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   worker_type_code VARCHAR(1) NULL,\n"
                        + "   worker_type_description VARCHAR(45) NULL\n"
                        + " )\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[1] =
                "CREATE TABLE IF NOT EXISTS workers (\n"
                        + "   worker_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   worker_type_id INTEGER NOT NULL,\n"
                        + "   worker_first_name VARCHAR(45) NULL,\n"
                        + "   worker_last_name VARCHAR(45) NULL,\n"
                        + "   consulting_privilege CHECK( consulting_privilege IN ('Y', 'N') ) DEFAULT 'N',\n"
                        + "   admitting_privilege CHECK( admitting_privilege IN ('Y', 'N') ) DEFAULT 'N',  \n"
                        + "   UNIQUE (worker_last_name ASC) ,\n"
                        + "   CONSTRAINT fk_workers_01\n"
                        + "     FOREIGN KEY (worker_type_id)\n"
                        + "     REFERENCES worker_types (worker_type_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION)\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[2] =
                "CREATE TABLE IF NOT EXISTS patients (\n"
                        + "   patient_id INTEGER PRIMARY KEY,\n"
                        + "   patient_first_name VARCHAR(45) NULL,\n"
                        + "   patient_last_name VARCHAR(45) NULL,\n"
                        + "   emergency_contact_name VARCHAR(45) NULL,\n"
                        + "   emergency_contact_number INTEGER NULL,\n"
                        + "   insurance_policy_number VARCHAR(45) NULL,\n"
                        + "   insurance_policy_company VARCHAR(45) NULL,\n"
                        + "   primary_physician_last_name VARCHAR(45) NULL,\n"
                        + "   UNIQUE (patient_last_name ASC) )\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[3] =
                "CREATE TABLE IF NOT EXISTS rooms (\n"
                        + "   room_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   room_designation VARCHAR(45) NULL,\n"
                        + "   room_number CHECK( room_number IN ('1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20') )   NOT NULL DEFAULT '1',\n"
                        + "   UNIQUE (room_number ASC)\n"
                        + " )\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[4] =
                "CREATE TABLE IF NOT EXISTS patient_rooms (\n"
                        + "   patient_room_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   patient_id INTEGER NULL,\n"
                        + "   room_id INTEGER NULL,\n"
                        + "   start_timestamp varchar(45) NULL,\n"
                        + "   end_timestamp varchar(45) NULL,\n"
                        + "   admitting_physician_id INTEGER NULL,\n"
                        + "   checkin_administrative_worker_id INTEGER NULL,\n"
                        + "   checkout_administrative_worker_id INTEGER NULL,\n"
                        + "   CONSTRAINT fk_patient_rooms_01\n"
                        + "     FOREIGN KEY (patient_id)\n"
                        + "     REFERENCES patients (patient_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION,\n"
                        + "   CONSTRAINT fk_patient_rooms_02\n"
                        + "     FOREIGN KEY (room_id)\n"
                        + "     REFERENCES rooms (room_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION,\n"
                        + "   CONSTRAINT fk_patient_rooms_03\n"
                        + "     FOREIGN KEY (admitting_physician_id)\n"
                        + "     REFERENCES workers (worker_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION,\n"
                        + "   CONSTRAINT fk_patient_rooms_04\n"
                        + "     FOREIGN KEY (checkin_administrative_worker_id)\n"
                        + "     REFERENCES workers (worker_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION,\n"
                        + "   CONSTRAINT fk_patient_rooms_05\n"
                        + "     FOREIGN KEY (checkout_administrative_worker_id)\n"
                        + "     REFERENCES workers (worker_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION)\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[5] =
                "CREATE TABLE IF NOT EXISTS services (\n"
                        + "   service_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   service_type CHECK( service_type IN ('inpatient', 'outpatient') ) NOT NULL DEFAULT 'outpatient',\n"
                        + "   service_short_description varchar(255) DEFAULT NULL,\n"
                        + "   service_long_description varchar(2000) DEFAULT NULL\n"
                        + " )\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[6] =
                "CREATE TABLE IF NOT EXISTS medications (\n"
                        + "   medication_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   medication_description VARCHAR(255) NOT NULL,\n"
                        + "   medication_dosage VARCHAR(255) NOT NULL,\n"
                        + "   medication_frequency VARCHAR(255) NOT NULL,\n"
                        + "   medication_aliases VARCHAR(255) NULL\n"
                        + " )\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[7] =
                "CREATE TABLE IF NOT EXISTS patient_doctors (\n"
                        + "   patient_doctor_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   patient_id INTEGER NULL,\n"
                        + "   doctor_id INTEGER NULL,\n"
                        + "   start_timestamp varchar(45) NULL,\n"
                        + "   end_timestamp varchar(45) NULL,\n"
                        + "   CONSTRAINT fk_patient_doctors_01\n"
                        + "     FOREIGN KEY (patient_id)\n"
                        + "     REFERENCES patients (patient_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION,\n"
                        + "   CONSTRAINT fk_patient_doctors_02\n"
                        + "     FOREIGN KEY (doctor_id)\n"
                        + "     REFERENCES workers (worker_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION)\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[8] =
                " CREATE TABLE IF NOT EXISTS treatments (\n"
                        + "   treatment_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   treatment_short_description varchar(255) DEFAULT NULL,\n"
                        + "   treatment_description varchar(2000) DEFAULT NULL,\n"
                        + "   associated_diagnosis varchar(45) DEFAULT NULL \n"
                        + " )\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[9] =
                "CREATE TABLE IF NOT EXISTS treatment_components (\n"
                        + "   treatment_component_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   treatment_id INTEGER NULL,\n"
                        + "   service_id INTEGER NULL,\n"
                        + "   medication_id INTEGER NULL,\n"
                        + "   CONSTRAINT fk_treatment_components_01\n"
                        + "     FOREIGN KEY (treatment_id)\n"
                        + "     REFERENCES treatments (treatment_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION,\n"
                        + "   CONSTRAINT fk_treatment_components_02\n"
                        + "     FOREIGN KEY (service_id)\n"
                        + "     REFERENCES services (service_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION,\n"
                        + "   CONSTRAINT fk_treatment_components_03\n"
                        + "     FOREIGN KEY (medication_id)\n"
                        + "     REFERENCES medications (medication_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION)\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[10] =
                "CREATE TABLE IF NOT EXISTS patient_treatment_components (\n"
                        + "   patient_treatment_component_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   patient_id INTEGER NOT NULL,\n"
                        + "   diagnosis_description VARCHAR(255) NULL,\n"
                        + "   initial_diagnosis CHECK(initial_diagnosis IN ('Y', 'N') ) NOT NULL DEFAULT 'N',\n"
                        + "   diagnosis_timestamp varchar(45) NULL,\n"
                        + "   treatment_start_timestamp varchar(45) NULL,\n"
                        + "   treatment_end_timestamp varchar(45) NULL,\n"
                        + "   treatment_component_id INTEGER NULL,\n"
                        + "   ordering_physician_id INTEGER NOT NULL,\n"
                        + "   CONSTRAINT fk_patient_treatment_components_01\n"
                        + "     FOREIGN KEY (patient_id)\n"
                        + "     REFERENCES patients (patient_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION,\n"
                        + "   CONSTRAINT fk_patient_treatment_components_02\n"
                        + "     FOREIGN KEY (treatment_component_id)\n"
                        + "     REFERENCES treatment_components (treatment_component_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION,\n"
                        + "   CONSTRAINT fk_patient_treatment_components_03\n"
                        + "     FOREIGN KEY (ordering_physician_id)\n"
                        + "     REFERENCES workers (worker_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION)\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[11] =
                "CREATE TABLE IF NOT EXISTS patient_treatment_administered (\n"
                        + "   patient_treatment_administered_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   patient_treatment_component_id INTEGER NULL,\n"
                        + "   worker_id INTEGER NULL,\n"
                        + "   adminstered_time_stamp VARCHAR(45) NULL,\n"
                        + "   CONSTRAINT fk_patient_treatment_administered_01\n"
                        + "     FOREIGN KEY (patient_treatment_component_id)\n"
                        + "     REFERENCES patient_treatment_components (patient_treatment_component_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION,\n"
                        + "   CONSTRAINT fk_patient_treatment_administered_02\n"
                        + "     FOREIGN KEY (worker_id)\n"
                        + "     REFERENCES workers (worker_id)\n"
                        + "     ON DELETE NO ACTION\n"
                        + "     ON UPDATE NO ACTION)\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[12] =
                "CREATE TABLE IF NOT EXISTS import_person_data (\n"
                        + "   import_person_data_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   person_type varchar(1) NOT NULL,\n"
                        + "   person_first_name varchar(45) DEFAULT NULL,\n"
                        + "   person_last_name varchar(45) DEFAULT NULL,\n"
                        + "   privilege_type varchar(1) DEFAULT NULL,\n"
                        + "   patient_id int DEFAULT NULL ,\n"
                        + "   room_number int DEFAULT NULL ,\n"
                        + "   emergency_contact_name varchar(45) DEFAULT NULL,\n"
                        + "   emergency_contact_number varchar(45) DEFAULT NULL,\n"
                        + "   insurance_policy_number varchar(45) DEFAULT NULL,\n"
                        + "   insurance_policy_company varchar(45) DEFAULT NULL,\n"
                        + "   primary_physician_last_name varchar(45) DEFAULT NULL,\n"
                        + "   initial_diagnosis varchar(255) DEFAULT NULL,\n"
                        + "   admission_date varchar(45) DEFAULT NULL ,\n"
                        + "   discharge_date varchar(45) DEFAULT NULL , \n"
                        + "   UNIQUE (person_last_name)\n"
                        + " )\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[13] =
                "CREATE TABLE IF NOT EXISTS history_import_person_data (\n"
                        + "   history_import_person_data_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   import_person_data_id INTEGER NOT NULL,\n"
                        + "   person_type varchar(1) NOT NULL,\n"
                        + "   person_first_name varchar(45) DEFAULT NULL,\n"
                        + "   person_last_name varchar(45) DEFAULT NULL,\n"
                        + "   privilege_type varchar(1) DEFAULT NULL,\n"
                        + "   patient_id int DEFAULT NULL ,\n"
                        + "   room_number int DEFAULT NULL ,\n"
                        + "   emergency_contact_name varchar(45) DEFAULT NULL,\n"
                        + "   emergency_contact_number varchar(45) DEFAULT NULL,\n"
                        + "   insurance_policy_number varchar(45) DEFAULT NULL,\n"
                        + "   insurance_policy_company varchar(45) DEFAULT NULL,\n"
                        + "   primary_physician_last_name varchar(45) DEFAULT NULL,\n"
                        + "   initial_diagnosis varchar(255) DEFAULT NULL,\n"
                        + "   admission_date varchar(45) DEFAULT NULL ,\n"
                        + "   discharge_date varchar(45) DEFAULT NULL , \n"
                        + "   history_type varchar(45) DEFAULT 'INSERT',\n"
                        + "   history_date DATETIME DEFAULT CURRENT_TIMESTAMP\n"
                        + " )\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[14] =
                "CREATE TABLE IF NOT EXISTS import_treatment_data (\n"
                        + "   import_treatment_data_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   patient_last_name varchar(45) DEFAULT NULL,\n"
                        + "   ordering_physician_last_name varchar(45) DEFAULT NULL,\n"
                        + "   treatment_type varchar(1) DEFAULT NULL,\n"
                        + "   short_description varchar(255) DEFAULT NULL,\n"
                        + "   timestamp varchar(45) DEFAULT NULL \n"
                        + " )\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[15] =
                "CREATE TABLE IF NOT EXISTS history_import_treatment_data (\n"
                        + "   history_import_treatment_data_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   import_treatment_data_id INTEGER NOT NULL,\n"
                        + "   patient_last_name varchar(45) DEFAULT NULL,\n"
                        + "   ordering_physician_last_name varchar(45) DEFAULT NULL,\n"
                        + "   treatment_type varchar(1) DEFAULT NULL,\n"
                        + "   short_description varchar(255) DEFAULT NULL,\n"
                        + "   timestamp varchar(45) DEFAULT NULL,\n"
                        + "   history_type varchar(45) DEFAULT 'INSERT',\n"
                        + "   history_date DATETIME DEFAULT CURRENT_TIMESTAMP  \n"
                        + " )\n"
                        + " ;\n"
        ;
        CREATE_TABLE_STRINGS[16] =
                "CREATE TABLE IF NOT EXISTS temp_keys \n"
                        + " (\n"
                        + "   temp_key_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "   p_worker_type_id INTEGER NOT NULL DEFAULT 0 ,\n"
                        + "   p_room_id INTEGER NOT NULL DEFAULT 0 ,\n"
                        + "   p_patient_id INTEGER NOT NULL DEFAULT 0 ,\n"
                        + "   p_physician_id INTEGER NOT NULL DEFAULT 0 ,\n"
                        + "   p_treatment_id INTEGER NOT NULL DEFAULT 0 ,\n"
                        + "   p_treatment_component_id INTEGER NOT NULL DEFAULT 0 ,\n"
                        + "   p_initial_diagnosis VARCHAR(1) NOT NULL DEFAULT 'Y' ,\n"
                        + "   p_diagnosis_description VARCHAR(255) NULL,\n"
                        + "   p_service_type VARCHAR(45) NOT NULL DEFAULT 'inpatient',\n"
                        + "   p_service_id INTEGER NOT NULL DEFAULT 0 ,\n"
                        + "   p_admission_date DATETIME DEFAULT NULL ,\n"
                        + "   p_discharge_date DATETIME DEFAULT NULL \n"
                        + " );\n"
        ;
    }

    // Database create statement
    String[] POPULATE_TABLE_LOOKUPS = new String[25];

    private void populatePopulateLookupTables() {
        // worker types
        POPULATE_TABLE_LOOKUPS[0] = "INSERT INTO worker_types VALUES(1,'V','Volunteer');\n";
        POPULATE_TABLE_LOOKUPS[1] = "INSERT INTO worker_types VALUES(2,'D','Doctor');\n";
        POPULATE_TABLE_LOOKUPS[2] = "INSERT INTO worker_types VALUES(3,'A','Administrator');\n";
        POPULATE_TABLE_LOOKUPS[3] = "INSERT INTO worker_types VALUES(4,'N','Nurse');\n";
        POPULATE_TABLE_LOOKUPS[4] = "INSERT INTO worker_types VALUES(5,'T','Technician');";

        // rooms
        POPULATE_TABLE_LOOKUPS[5] = "INSERT INTO rooms VALUES(1,'Urgent Care','1');\n";
        POPULATE_TABLE_LOOKUPS[6] = "INSERT INTO rooms VALUES(2,'Urgent Care','2');\n";
        POPULATE_TABLE_LOOKUPS[7] = "INSERT INTO rooms VALUES(3,'Urgent Care','3');\n";
        POPULATE_TABLE_LOOKUPS[8] = "INSERT INTO rooms VALUES(4,'Maternity','4');\n";
        POPULATE_TABLE_LOOKUPS[9] = "INSERT INTO rooms VALUES(5,'Maternity','5');\n";
        POPULATE_TABLE_LOOKUPS[10] = "INSERT INTO rooms VALUES(6,'General Purpose','6');\n";
        POPULATE_TABLE_LOOKUPS[11] = "INSERT INTO rooms VALUES(7,'General Purpose','7');\n";
        POPULATE_TABLE_LOOKUPS[12] = "INSERT INTO rooms VALUES(8,'General Purpose','8');\n";
        POPULATE_TABLE_LOOKUPS[13] = "INSERT INTO rooms VALUES(9,'General Purpose','9');\n";
        POPULATE_TABLE_LOOKUPS[14] = "INSERT INTO rooms VALUES(10,'General Purpose','10');\n";
        POPULATE_TABLE_LOOKUPS[15] = "INSERT INTO rooms VALUES(11,'General Purpose','11');\n";
        POPULATE_TABLE_LOOKUPS[16] = "INSERT INTO rooms VALUES(12,'General Purpose','12');\n";
        POPULATE_TABLE_LOOKUPS[17] = "INSERT INTO rooms VALUES(13,'General Purpose','13');\n";
        POPULATE_TABLE_LOOKUPS[18] = "INSERT INTO rooms VALUES(14,'General Purpose','14');\n";
        POPULATE_TABLE_LOOKUPS[19] = "INSERT INTO rooms VALUES(15,'General Purpose','15');\n";
        POPULATE_TABLE_LOOKUPS[20] = "INSERT INTO rooms VALUES(16,'General Purpose','16');\n";
        POPULATE_TABLE_LOOKUPS[21] = "INSERT INTO rooms VALUES(17,'General Purpose','17');\n";
        POPULATE_TABLE_LOOKUPS[22] = "INSERT INTO rooms VALUES(18,'General Purpose','18');\n";
        POPULATE_TABLE_LOOKUPS[23] = "INSERT INTO rooms VALUES(19,'Recovery','19');\n";
        POPULATE_TABLE_LOOKUPS[24] = "INSERT INTO rooms VALUES(20,'Recovery','20');";

    }

    public void deleteFile(String fileToDelete) {
        File myObj = new File(fileToDelete);
        if (myObj.delete()) {
            System.out.println("Deleted the database: " + myObj.getName());
        } else {
            System.out.println("Failed to delete the database:" + myObj.getName());
        }
    }

    public int checkFile(String fileToCheck) {
        File myObj = new File(fileToCheck);
        int returnValue;
        if (myObj.exists()) {
            System.out.println("Database already exists: " + myObj.getName());
            returnValue = 1;
        } else {
            System.out.println("Database does not exist: " + myObj.getName());
            returnValue = 0;
        }
        return returnValue;
    }


}