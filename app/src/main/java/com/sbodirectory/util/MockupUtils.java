package com.sbodirectory.util;

import com.sbodirectory.model.Company;
import com.sbodirectory.model.Location;

/**
 * Created by AnhNDT on 3/29/15.
 */
public class MockupUtils {
    public static Company makeCompany() {
        Company company = new Company();
        company.name = "company 1";
        company.address = "Address 1";
        company.country = "Viet nam";
        company.website = "https://google.com";
        company.phone = "(+84)1649593518";
        company.location = new Location(37, -122);
        company.thumbnail = "http://s.f31.img.vnecdn.net/2015/03/29/mb-8099-1427593644-3781-1427593828_180x108.jpg";
        return company;
    }
    public static Location makeLocation() {
        return new Location(38, -123);
    }
}
