# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.views.generic import View
from django.shortcuts import render_to_response

from fuzzyapp.models import Materia
from fuzzyapp.forms import FiltroMateriasForm


class ListaMateriasView(View):

    def get(self, request):
        # Por default lista todas las materias y muestra un formulario
        # para filtrarlas y ordenarlas.
        materias = Materia.objects.all()
        form = FiltroMateriasForm()
        return render_to_response(
            "fuzzyapp/lista_materias.html",
            {"materias": list(materias), "form": form}
        )

    def post(self, request):
        # Filtra y ordena las materias si el formulario era válido
        # sino, por default hace los mismo que GET
        form = FiltroMateriasForm(request.POST)
        materias = Materia.objects.all()
        if form.is_valid():
            if form.cleaned_data["filtrar_dptos"]:
                materias.filter(
                    codigo__startswithany=form.cleaned_data["dptos"]
                )
            orden1 = form.cleaned_data["orden1"]
            orden2 = form.cleaned_data["orden2"]
            orden3 = form.cleaned_data["orden3"]
            if orden1 != '':
                materias.order_by(orden1, direction=form.cleaned_data["asc1"])
            if orden2 != '':
                materias.order_by(orden2, direction=form.cleaned_data["asc2"])
            if orden3 != '':
                materias.order_by(orden3, direction=form.cleaned_data["asc3"])

        return render_to_response(
            "fuzzyapp/lista_materias.html",
            {"materias": list(materias), "form": form}
        )
