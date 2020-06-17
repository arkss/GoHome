package com.uos.gohome.main;

import com.uos.gohome.retrofit2.Datum;

// fragment간 통신을 위한 인터페이스
public interface OnDataSendListener {
    void setDatum(Datum datum);
}
