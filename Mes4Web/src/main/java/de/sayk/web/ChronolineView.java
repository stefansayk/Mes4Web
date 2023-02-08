package de.sayk.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean
@ApplicationScoped
public class ChronolineView {

    private List<Event> events;

    @PostConstruct
    public void init() {
        events = new ArrayList<>();
        events.add(new Event("Lager", "15/10/2020 10:30", "pi pi-check", "#088A4B"));
        events.add(new Event("Wenden", "15/10/2020 14:00", "pi pi-cog", "#B40404"));
        events.add(new Event("Drucken", "15/10/2020 16:15", "pi pi-circle", "#E6E6E6"));
        events.add(new Event("Ofen", "16/10/2020 10:00", "pi pi-circle", "#E6E6E6"));
        events.add(new Event("Lager", "16/10/2020 10:00", "pi pi-circle", "#E6E6E6"));

    }

    public List<Event> getEvents() {
        return events;
    }


    public static class Event {
        private String status;
        private String date;
        private String icon;
        private String color;
        private String image;

        public Event() {
        }

        public Event(String status, String date, String icon, String color) {
            this.status = status;
            this.date = date;
            this.icon = icon;
            this.color = color;
        }

        public Event(String status, String date, String icon, String color, String image) {
            this.status = status;
            this.date = date;
            this.icon = icon;
            this.color = color;
            this.image = image;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

}