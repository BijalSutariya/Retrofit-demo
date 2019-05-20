package com.example.loginregistration.map;

interface DrawableClickListener {

    enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT }
    void onClick(DrawablePosition target);

}
