package com.leoPirpiri.protournamentmanager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class NavegacaoPorPaginasAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentosList;
    private final List<String> titulosList;

    public NavegacaoPorPaginasAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragmentosList, List<String> titulosList) {
        super(fragmentActivity);
        this.fragmentosList = fragmentosList;
        this.titulosList = titulosList;
    }

    public String getTitulo(int posicao){
        return titulosList.get(posicao);
    }

    @NonNull
    @Override
    public Fragment createFragment(int posicao) {
        return fragmentosList.get(posicao);
    }

    @Override
    public int getItemCount() {
        return titulosList.size();
    }
}
