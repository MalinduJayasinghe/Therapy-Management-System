module lk.ijse.therapy_management_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.hibernate.orm.core;
    requires jakarta.persistence;

    requires lombok;

    requires net.sf.jasperreports.core;

    requires java.naming;
    requires java.sql;
    requires bcrypt;

    opens lk.ijse.therapy_management_system to javafx.fxml;
    opens lk.ijse.therapy_management_system.controller to javafx.fxml;
    opens lk.ijse.therapy_management_system.entity to org.hibernate.orm.core, javafx.base;
    opens lk.ijse.therapy_management_system.dto to javafx.base;
    opens lk.ijse.therapy_management_system.dto.tm to javafx.base;

    exports lk.ijse.therapy_management_system;
    exports lk.ijse.therapy_management_system.controller;
    exports lk.ijse.therapy_management_system.entity;
    exports lk.ijse.therapy_management_system.dto;
    exports lk.ijse.therapy_management_system.dto.tm;
    exports lk.ijse.therapy_management_system.bo;
    exports lk.ijse.therapy_management_system.bo.custom;
    exports lk.ijse.therapy_management_system.dao;
    exports lk.ijse.therapy_management_system.dao.custom;
    exports lk.ijse.therapy_management_system.exception;
    exports lk.ijse.therapy_management_system.util;
    exports lk.ijse.therapy_management_system.config;
}