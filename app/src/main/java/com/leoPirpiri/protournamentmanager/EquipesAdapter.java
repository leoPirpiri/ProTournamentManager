package com.leoPirpiri.protournamentmanager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import model.Equipe;

public class EquipesAdapter extends BaseAdapter {
    private ArrayList<Equipe> equipes;
    private Context ctx;

    public EquipesAdapter(Context ctx, ArrayList<Equipe> equipes) {
        this.equipes = equipes;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
