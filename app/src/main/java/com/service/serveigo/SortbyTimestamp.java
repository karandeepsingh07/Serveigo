package com.service.serveigo;

import java.util.Comparator;

class SortbyTimestamp implements Comparator<ClassBooking>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(ClassBooking a, ClassBooking b)
    {
        return (b.getDate()+" "+b.getTime()).compareTo(a.getDate()+" "+a.getTime());
    }
}