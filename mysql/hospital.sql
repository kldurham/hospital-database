-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema hospital
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema hospital
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `hospital` DEFAULT CHARACTER SET utf8 ;
USE `hospital` ;

-- -----------------------------------------------------
-- Table `hospital`.`import_person_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`import_person_data` (
  `import_person_data_id` INT NOT NULL AUTO_INCREMENT,
  `person_type` VARCHAR(1) NOT NULL,
  `person_first_name` VARCHAR(45) NULL DEFAULT NULL,
  `person_last_name` VARCHAR(45) NULL DEFAULT NULL,
  `privilege_type` VARCHAR(1) NULL DEFAULT NULL,
  `patient_id` INT NULL DEFAULT NULL COMMENT 'allow nulls because may not be a patient',
  `room_number` INT NULL DEFAULT NULL COMMENT 'allow nulls',
  `emergency_contact_name` VARCHAR(45) NULL DEFAULT NULL,
  `emergency_contact_number` VARCHAR(45) NULL DEFAULT NULL,
  `insurance_policy_number` VARCHAR(45) NULL DEFAULT NULL,
  `insurance_policy_company` VARCHAR(45) NULL DEFAULT NULL,
  `primary_physician_last_name` VARCHAR(45) NULL DEFAULT NULL,
  `initial_diagnosis` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL,
  `admission_date` VARCHAR(45) NULL DEFAULT NULL COMMENT 'need to parse\\nformat m-d-yyyy',
  `discharge_date` VARCHAR(45) NULL DEFAULT NULL COMMENT 'need to parse\\nformat m-d-yyyy',
  PRIMARY KEY (`import_person_data_id`),
  UNIQUE INDEX `person_last_name_UNIQUE` (`person_last_name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 26
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`import_treatment_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`import_treatment_data` (
  `import_treatment_data_id` INT NOT NULL AUTO_INCREMENT,
  `patient_last_name` VARCHAR(45) NULL DEFAULT NULL,
  `ordering_physician_last_name` VARCHAR(45) NULL DEFAULT NULL,
  `treatment_type` VARCHAR(1) NULL DEFAULT NULL,
  `timestamp` VARCHAR(45) NULL DEFAULT NULL COMMENT 'need to parse',
  PRIMARY KEY (`import_treatment_data_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`medications`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`medications` (
  `medication_id` INT NOT NULL AUTO_INCREMENT,
  `medication_description` VARCHAR(255) NULL DEFAULT NULL,
  `medication_dosage` VARCHAR(255) NULL DEFAULT NULL,
  `medication_frequency` VARCHAR(255) NULL DEFAULT NULL,
  `medication_aliases` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`medication_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`patients`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`patients` (
  `patient_id` INT NOT NULL AUTO_INCREMENT,
  `patient_first_name` VARCHAR(45) NULL DEFAULT NULL,
  `patient_last_name` VARCHAR(45) NULL DEFAULT NULL,
  `emergency_contact_name` VARCHAR(45) NULL DEFAULT NULL,
  `emergency_contact_number` VARCHAR(20) NULL DEFAULT NULL,
  `insurance_policy_number` VARCHAR(45) NULL DEFAULT NULL,
  `insurance_policy_company` VARCHAR(45) NULL DEFAULT NULL,
  `primary_physician_last_name` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`patient_id`),
  UNIQUE INDEX `patient_last_name_UNIQUE` (`patient_last_name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 124
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`worker_types`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`worker_types` (
  `worker_type_id` INT NOT NULL AUTO_INCREMENT,
  `worker_type_code` VARCHAR(1) NULL DEFAULT NULL,
  `worker_type_description` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`worker_type_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`workers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`workers` (
  `worker_id` INT NOT NULL AUTO_INCREMENT,
  `worker_type_id` INT NOT NULL,
  `worker_first_name` VARCHAR(45) NULL DEFAULT NULL,
  `worker_last_name` VARCHAR(45) NULL DEFAULT NULL,
  `consulting_privilege` ENUM('Y', 'N') NOT NULL DEFAULT 'N',
  `admitting_privilege` ENUM('Y', 'N') NOT NULL DEFAULT 'N',
  PRIMARY KEY (`worker_id`),
  UNIQUE INDEX `worker_last_name_UNIQUE` (`worker_last_name` ASC) VISIBLE,
  INDEX `fk_workers_01_idx` (`worker_type_id` ASC) VISIBLE,
  CONSTRAINT `fk_workers_01`
    FOREIGN KEY (`worker_type_id`)
    REFERENCES `hospital`.`worker_types` (`worker_type_id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`patient_doctors`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`patient_doctors` (
  `patient_doctor_id` INT NOT NULL AUTO_INCREMENT,
  `patient_id` INT NULL DEFAULT NULL,
  `doctor_id` INT NULL DEFAULT NULL,
  `start_timestamp` DATETIME NULL DEFAULT NULL,
  `end_timestamp` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`patient_doctor_id`),
  INDEX `fk_patient_doctors_01_idx` (`patient_id` ASC) VISIBLE,
  INDEX `fk_patient_doctors_02_idx` (`doctor_id` ASC) VISIBLE,
  CONSTRAINT `fk_patient_doctors_01`
    FOREIGN KEY (`patient_id`)
    REFERENCES `hospital`.`patients` (`patient_id`),
  CONSTRAINT `fk_patient_doctors_02`
    FOREIGN KEY (`doctor_id`)
    REFERENCES `hospital`.`workers` (`worker_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 20
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`rooms`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`rooms` (
  `room_id` INT NOT NULL AUTO_INCREMENT,
  `room_designation` VARCHAR(45) NULL DEFAULT NULL,
  `room_number` ENUM('1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20') NULL DEFAULT NULL,
  PRIMARY KEY (`room_id`),
  UNIQUE INDEX `room_number_UNIQUE` (`room_number` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 21
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`patient_rooms`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`patient_rooms` (
  `patient_room_id` INT NOT NULL AUTO_INCREMENT,
  `patient_id` INT NULL DEFAULT NULL,
  `room_id` INT NULL DEFAULT NULL,
  `start_timestamp` DATETIME NULL DEFAULT NULL,
  `end_timestamp` DATETIME NULL DEFAULT NULL,
  `admitting_physician_id` INT NULL DEFAULT NULL,
  `checkin_administrative_worker_id` INT NULL DEFAULT NULL,
  `checkout_administrative_worker_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`patient_room_id`),
  INDEX `fk_patient_rooms_01_idx` (`patient_id` ASC) VISIBLE,
  INDEX `fk_patient_rooms_02_idx` (`room_id` ASC) VISIBLE,
  INDEX `fk_patient_rooms_03_idx` (`admitting_physician_id` ASC) VISIBLE,
  INDEX `fk_patient_rooms_04_idx` (`checkin_administrative_worker_id` ASC) VISIBLE,
  INDEX `fk_patient_rooms_05_idx` (`checkout_administrative_worker_id` ASC) VISIBLE,
  CONSTRAINT `fk_patient_rooms_01`
    FOREIGN KEY (`patient_id`)
    REFERENCES `hospital`.`patients` (`patient_id`),
  CONSTRAINT `fk_patient_rooms_02`
    FOREIGN KEY (`room_id`)
    REFERENCES `hospital`.`rooms` (`room_id`),
  CONSTRAINT `fk_patient_rooms_03`
    FOREIGN KEY (`admitting_physician_id`)
    REFERENCES `hospital`.`workers` (`worker_id`),
  CONSTRAINT `fk_patient_rooms_04`
    FOREIGN KEY (`checkin_administrative_worker_id`)
    REFERENCES `hospital`.`workers` (`worker_id`),
  CONSTRAINT `fk_patient_rooms_05`
    FOREIGN KEY (`checkout_administrative_worker_id`)
    REFERENCES `hospital`.`workers` (`worker_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 20
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`treatments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`treatments` (
  `treatment_id` INT NOT NULL AUTO_INCREMENT,
  `treatment_short_description` VARCHAR(255) NULL DEFAULT NULL,
  `treatment_description` VARCHAR(2000) NULL DEFAULT NULL,
  `associated_diagnosis` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`treatment_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 15
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`services`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`services` (
  `service_id` INT NOT NULL AUTO_INCREMENT,
  `service_type` ENUM('inpatient', 'outpatient') NULL DEFAULT NULL,
  `service_short_description` VARCHAR(255) NULL DEFAULT NULL,
  `service_long_description` VARCHAR(2000) NULL DEFAULT NULL,
  PRIMARY KEY (`service_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 14
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`treatment_components`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`treatment_components` (
  `treatment_component_id` INT NOT NULL AUTO_INCREMENT,
  `treatment_id` INT NULL DEFAULT NULL,
  `service_id` INT NULL DEFAULT NULL,
  `medication_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`treatment_component_id`),
  INDEX `fk_treatment_components_01_idx` (`treatment_id` ASC) VISIBLE,
  INDEX `fk_treatment_components_02_idx` (`service_id` ASC) VISIBLE,
  INDEX `fk_treatment_components_03_idx` (`medication_id` ASC) VISIBLE,
  CONSTRAINT `fk_treatment_components_01`
    FOREIGN KEY (`treatment_id`)
    REFERENCES `hospital`.`treatments` (`treatment_id`),
  CONSTRAINT `fk_treatment_components_02`
    FOREIGN KEY (`service_id`)
    REFERENCES `hospital`.`services` (`service_id`),
  CONSTRAINT `fk_treatment_components_03`
    FOREIGN KEY (`medication_id`)
    REFERENCES `hospital`.`medications` (`medication_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 14
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`patient_treatment_components`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`patient_treatment_components` (
  `patient_treatment_component_id` INT NOT NULL AUTO_INCREMENT,
  `patient_id` INT NULL DEFAULT NULL,
  `diagnosis_description` VARCHAR(2000) NULL DEFAULT NULL,
  `initial_diagnosis` ENUM('Y', 'N') NULL DEFAULT 'N',
  `diagnosis_timestamp` DATETIME NULL DEFAULT NULL,
  `treatment_start_timestamp` DATETIME NULL DEFAULT NULL,
  `treatment_end_timestamp` DATETIME NULL DEFAULT NULL,
  `treatment_component_id` INT NULL DEFAULT NULL,
  `ordering_physician_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`patient_treatment_component_id`),
  INDEX `fk_patient_treatment_components_01_idx` (`patient_id` ASC) VISIBLE,
  INDEX `fk_patient_treatment_components_02_idx` (`treatment_component_id` ASC) VISIBLE,
  INDEX `fk_patient_treatment_components_03_idx` (`ordering_physician_id` ASC) VISIBLE,
  CONSTRAINT `fk_patient_treatment_components_01`
    FOREIGN KEY (`patient_id`)
    REFERENCES `hospital`.`patients` (`patient_id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_patient_treatment_components_02`
    FOREIGN KEY (`treatment_component_id`)
    REFERENCES `hospital`.`treatment_components` (`treatment_component_id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_patient_treatment_components_03`
    FOREIGN KEY (`ordering_physician_id`)
    REFERENCES `hospital`.`workers` (`worker_id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `hospital`.`patient_treatment_administered`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hospital`.`patient_treatment_administered` (
  `patient_treatment_administered_id` INT NOT NULL AUTO_INCREMENT,
  `patient_treatment_component_id` INT NULL DEFAULT NULL,
  `worker_id` INT NULL DEFAULT NULL,
  `adminstered_time_stamp` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`patient_treatment_administered_id`),
  INDEX `fk_patient_treatment_administered_01_idx` (`patient_treatment_component_id` ASC) VISIBLE,
  INDEX `fk_patient_treatment_administered_02_idx` (`worker_id` ASC) VISIBLE,
  CONSTRAINT `fk_patient_treatment_administered_01`
    FOREIGN KEY (`patient_treatment_component_id`)
    REFERENCES `hospital`.`patient_treatment_components` (`patient_treatment_component_id`),
  CONSTRAINT `fk_patient_treatment_administered_02`
    FOREIGN KEY (`worker_id`)
    REFERENCES `hospital`.`workers` (`worker_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

USE `hospital`;

DELIMITER $$
USE `hospital`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `hospital`.`trg_import_person_data_01`
AFTER INSERT ON `hospital`.`import_person_data`
FOR EACH ROW
BEGIN
       		-- set temp keys
       		SET @treatment_component_id = 0;
       		SET @worker_type_id = 0;
       		SET @room_id = 0;
       		SET @physician_id = 0;
       		SET @treatment_id = 0;
          SET @initial_diagnosis = NEW.initial_diagnosis;
          
       		-- populate temp keys
      		SELECT worker_type_id INTO @worker_type_id FROM worker_types WHERE worker_type_code=NEW.person_type;
      		SELECT room_id INTO @room_id FROM rooms WHERE room_number=NEW.room_number;
      		SELECT worker_id INTO @physician_id FROM workers WHERE worker_last_name = NEW.primary_physician_last_name;
      		SELECT treatment_id INTO @treatment_id FROM treatments WHERE associated_diagnosis = NEW.initial_diagnosis;

          -- clean up dates
          SET @admission_date = (SELECT STR_TO_DATE(NEW.admission_date,'%m-%d-%Y'));
          SET @discharge_date = (SELECT STR_TO_DATE(NEW.discharge_date,'%m-%d-%Y'));
         

		-- if treatment is not in system, record it
    IF @treatment_id = 0 
    THEN
        IF LENGTH(@initial_diagnosis) > 1
    		THEN 
    			INSERT INTO treatments
    			(treatment_short_description, associated_diagnosis)
    			VALUES
    			(CONCAT(NEW.initial_diagnosis, ' Treatment'), NEW.initial_diagnosis)
    			;
    			SELECT LAST_INSERT_ID() INTO @treatment_id;
    			CASE NEW.person_type
      			WHEN 'I' THEN
      			  SET @service_type = 'inpatient';
      			WHEN 'O' THEN 
      			  SET @service_type = 'outpatient';
    			END CASE;
    			
    			INSERT INTO services
    			(service_type, service_short_description)
    			VALUES
    			(@service_type, CONCAT('Fix ', NEW.initial_diagnosis))
    			;
    			SELECT LAST_INSERT_ID() INTO @service_id;			
    			INSERT INTO treatment_components -- dummy record
    			(treatment_id, service_id)
    			VALUES
    			(@treatment_id, @service_id)
    			;
    			SELECT LAST_INSERT_ID() INTO @treatment_component_id;
    		END IF;
    END IF;
		-- parse data into tables
		CASE NEW.person_type
		WHEN 'O' THEN -- outpatient
			INSERT INTO patients
			(patient_id, patient_first_name, patient_last_name)
			VALUES
			(NEW.patient_id, NEW.person_first_name, NEW.person_last_name)
			ON DUPLICATE KEY UPDATE 
					  patient_first_name = NEW.person_first_name
  				, patient_last_name = NEW.person_last_name;
			
		WHEN 'I' THEN -- inpatient
			INSERT INTO patients
			(patient_id, patient_first_name, patient_last_name, emergency_contact_name
			, emergency_contact_number, insurance_policy_number, insurance_policy_company
			, primary_physician_last_name)
			VALUES
			(NEW.patient_id, NEW.person_first_name, NEW.person_last_name, NEW.emergency_contact_name
			, NEW.emergency_contact_number, NEW.insurance_policy_number, NEW.insurance_policy_company
			, NEW.primary_physician_last_name 
			)
			ON DUPLICATE KEY UPDATE 
					  patient_first_name = NEW.person_first_name
					, patient_last_name = NEW.person_last_name
					, emergency_contact_name = NEW.emergency_contact_name
					, emergency_contact_number = NEW.emergency_contact_number
					, insurance_policy_number = NEW.insurance_policy_number
					, insurance_policy_company = NEW.insurance_policy_company
					, primary_physician_last_name = NEW.primary_physician_last_name 
  			;	
  			INSERT INTO patient_rooms
  			(patient_id, room_id, start_timestamp, end_timestamp, admitting_physician_id)
  			VALUES
			  (NEW.patient_id, @room_id, @admission_date, @discharge_date, @physician_id) 
  			;
  			INSERT INTO patient_doctors
  			(patient_id, doctor_id, start_timestamp, end_timestamp)
  			VALUES
  			(NEW.patient_id, @physician_id, @admission_date, @discharge_date)
  			;
  			IF @treatment_component_id = 0
  			THEN 
  				SELECT MIN(treatment_component_id)
  				INTO @treatment_component_id
  				FROM treatment_components
  				WHERE treatment_id = @treatment_id ;
  			END IF;
  			INSERT INTO patient_treatment_components
  			(patient_id, diagnosis_description, initial_diagnosis, diagnosis_timestamp
  			, treatment_start_timestamp, treatment_end_timestamp, treatment_component_id
  			, ordering_physician_id )
  			VALUES
  			(NEW.patient_id, NEW.initial_diagnosis, 'Y', @admission_date
  			, @admission_date, @discharge_date, @treatment_component_id, @physician_id)
  			;
  			
		ELSE 	-- hospital worker		
			INSERT INTO workers (worker_type_id, worker_first_name, worker_last_name) 
				     VALUES (@worker_type_id, NEW.person_first_name, NEW.person_last_name)
			ON DUPLICATE KEY UPDATE worker_type_id = @worker_type_id
					      , worker_first_name = NEW.person_first_name;		
			IF (NEW.person_type = 'D') THEN 
				CASE NEW.privilege_type
				WHEN 'C' THEN -- Doctor
					UPDATE workers SET consulting_privilege = 'Y'
					WHERE worker_last_name =  NEW.person_last_name;
				WHEN 'A' THEN -- Doctor
					UPDATE workers SET admitting_privilege = 'Y'
					WHERE worker_last_name =  NEW.person_last_name;		
				END CASE ;	
			END IF;
		END CASE;
		
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
