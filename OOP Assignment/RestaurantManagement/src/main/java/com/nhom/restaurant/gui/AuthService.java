package com.nhom.restaurant.gui;

import com.nhom.restaurant.models.Employees;
import java.util.prefs.Preferences;
public class AuthService {
    private static final String PREF_NODE_NAME = "com/nhom/restaurant";
    private static final String KEY_EMPLOYEE_ID = "loggedInEmployeeId";

    public static void saveLogin(int employeeId) {
        try {
            Preferences prefs = Preferences.userRoot().node(PREF_NODE_NAME);
            prefs.putInt(KEY_EMPLOYEE_ID, employeeId);
            prefs.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearLogin() {
        try {
            Preferences prefs = Preferences.userRoot().node(PREF_NODE_NAME);
            prefs.remove(KEY_EMPLOYEE_ID);
            prefs.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Employees tryAutoLogin() {
        try {
            Preferences prefs = Preferences.userRoot().node(PREF_NODE_NAME);

            int savedId = prefs.getInt(KEY_EMPLOYEE_ID, -1);
            if (savedId != -1) {
                Employees employee = Employees.findById(savedId);
                if (employee != null) {
                    return employee;
                }
                else {
                    clearLogin();
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

