package com.freshworks.ems.util;
import com.freshworks.ems.model.Employee;

import java.util.Comparator;

public class AgeSorter implements Comparator<Employee>{
    @Override
    public int compare(Employee o1, Employee o2) {
        return o1.getAge() - o2.getAge();
    }
}
