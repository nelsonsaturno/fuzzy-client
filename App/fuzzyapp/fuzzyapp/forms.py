# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django import forms

from fuzzyapp.database import fuzzyQuery


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


def filtros_dptos():
    query = "SELECT id_unidad, nombre FROM opinion.unidad WHERE mostrar = 'Y'"
    columns = {
        "id_unidad": {"type": "integer"},
        "nombre": {"type": "string"}
    }
    result = []
    for row in fuzzyQuery(query, columns):
        result.append((row['id_unidad'], row['nombre']))
    return result


class FiltroMateriasForm(forms.Form):
    """
    Formulario para filtrar y ordenar las materias de opinión estudiantil.
    Permite elegir cuáles departamentos mostrar (basado en sus códigos)
    y hasta 3 campos sobre los cuales ordenar el resultado.
    """
    filtrar_dptos = forms.BooleanField(initial=False, required=False)
    dptos = forms.MultipleChoiceField(choices=filtros_dptos(), required=False)
    orden1 = forms.ChoiceField(choices=campos_ordenamiento, required=False)
    asc1 = forms.ChoiceField(choices=direccion, required=False, initial='DESC')
    orden2 = forms.ChoiceField(choices=campos_ordenamiento, required=False)
    asc2 = forms.ChoiceField(choices=direccion, required=False, initial='DESC')
    orden3 = forms.ChoiceField(choices=campos_ordenamiento, required=False)
    asc3 = forms.ChoiceField(choices=direccion, required=False, initial='DESC')


class AgruparMateriasForm(forms.Form):
    campo = forms.ChoiceField(choices=campos_ordenamiento)
