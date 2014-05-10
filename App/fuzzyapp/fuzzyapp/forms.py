# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django import forms


campos_ordenamiento = (
    ('', "---"),
    ('calificacion', "Calificación esperada"),
    ('preparacion', "Preparación previa"),
    ('dificultad', "Dificultad del curso")
)


direccion = (
    ('ASC', "Ascendente"),
    ('DESC', "Descendente")
)

filtros_dptos = (
    ('CI', "Computación"),
    ('PS', "Procesos y sistemas"),
)


class FiltroMateriasForm(forms.Form):
    """
    Formulario para filtrar y ordenar las materias de opinión estudiantil.
    Permite elegir cuáles departamentos mostrar (basado en sus códigos)
    y hasta 3 campos sobre los cuales ordenar el resultado.
    """
    filtrar_dptos = forms.BooleanField(initial=False)
    dptos = forms.MultipleChoiceField(choices=filtros_dptos, required=False)
    orden1 = forms.ChoiceField(choices=campos_ordenamiento, required=False)
    asc1 = forms.ChoiceField(choices=direccion, required=False, initial='DESC')
    orden2 = forms.ChoiceField(choices=campos_ordenamiento, required=False)
    asc2 = forms.ChoiceField(choices=direccion, required=False, initial='DESC')
    orden3 = forms.ChoiceField(choices=campos_ordenamiento, required=False)
    asc3 = forms.ChoiceField(choices=direccion, required=False, initial='DESC')
