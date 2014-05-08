import re

from django.conf import settings


# Fuzzy column converter


def convert_fuzzy(string, subtype_converter):
    # Drop the parentheses
    string = string[1:-1]
    match = re.match("\"\{(.*)\}\",\"\{(.*)\}\",(t|f)", string)

    odd, value, type = match.groups()
    type = True if type == "t" else False

    odd = map(float, odd.rsplit(","))
    value = map(
        lambda x: subtype_converter(x) if x != "NULL" else None,
        value.rsplit(",")
    )

    if type:
        return FuzzyExtension(zip(odd, value))
    else:
        return FuzzyTrapezoid(zip(odd, value))


class FuzzyValue(object):
    pass


class FuzzyExtension(FuzzyValue):

    def __init__(self, values):
        self.values = values

    def __str__(self):
        return str(list(self.values))

    def __unicode__(self):
        return unicode(list(self.values))


class FuzzyTrapezoid(FuzzyValue):

    def __init__(self, values):
        self.values = values
        self.x1 = values[0][1]
        self.x2 = values[1][1]
        self.x3 = values[2][1]
        self.x4 = values[3][1]

    def __str__(self):
        return str(list(self.values))

    def __unicode__(self):
        return str(list(self.values))


# Row fetcher and converter


def fetch_column(result_set, cname, type, **kwargs):
    conversion = c_conversions.get(
        type,
        lambda x, y, **kwargs: x.getObject(y).toString()
    )
    return conversion(result_set, cname, **kwargs)


c_conversions = {
    "integer": lambda x, y, **kwargs: x.getInt(y),
    "string": lambda x, y, **kwargs: x.getString(y),
    "boolean": lambda x, y, **kwargs: x.getBoolean(y),
    "fuzzy": lambda x, y, **kwargs: convert_fuzzy(x.getObject(y).toString(), **kwargs), # NOQA
    "default": lambda x, y, **kwargs: x.getObject(y).toString(),
}


def fuzzyQuery(query, columns={}):
    res = settings.FUZZYDB.execute(query).result
    while res.next():
        # Build row
        yield {
            cname: fetch_column(res, cname, **params)
            for cname, params in columns.items()
        }


def fuzzyStatement(statement):
    return settings.FUZZYDB.execute(statement)


## Ejemplo
# test_q = {
#     "nombre": {"type": "string", },
#     "apellido": {"type": "string", },
#     "edad": {"type": "fuzzy", "subtype_converter": int},
#     "sueldo": {"type": "integer", }
# }

# res = list(fuzzyQuery("SELECT * FROM personas", columns=test_q))
