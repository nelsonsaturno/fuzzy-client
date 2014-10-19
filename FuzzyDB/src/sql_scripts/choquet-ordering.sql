-- File: sugeno-ordering.sql
-- Description: This file contains the stored functions for comparing two
--              fuzzy type II values based on the Choquet integral. 

-- Definition of lower element '<'
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_choquet_lower(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        comp1 float := 0;
        comp2 float := 0;
        size1 int := array_length(elem1.value,1);
        size2 int := array_length(elem2.value,1);
    BEGIN
        -- Both numbers are expressed by extension
        IF elem1.type and elem2.type THEN
            FOR j IN 0..size1 LOOP
                FOR i IN 0..size2 LOOP
                    IF elem2.value[i] > elem1.value[j] THEN
                        comp1 := comp1 + (elem2.odd[i] * elem1.odd[j]);
                    END IF;
                END LOOP;
            END LOOP;

            size1 = size2;
            size2 = array_length(elem1.value,1);

            FOR j IN 0..size1 LOOP
                FOR i IN 0..size2 LOOP
                    IF elem1.value[i] > elem2.value[j] THEN
                        comp2 := comp2 + (elem1.odd[i] * elem2.odd[j]);
                    END IF;
                END LOOP;
            END LOOP;

            return comp1 > comp2;

        -- Both numbers are expressed by trapezoids
        ELSE IF (elem1.type = False) and (elem2.type = False) THEN
            IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
                IF (elem1.value[2] is not Null) THEN
                    IF elem1.value[2] > elem2.value[3] THEN
                        return False;
                    END IF;

                    return False;			
                END IF;
            END IF;

            IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
                IF elem1.value[3] < elem2.value[2] THEN
                    return True;		
                END IF;

                IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
                    return False;
                END IF;

                IF elem1.value[2] > elem2.value[3]  THEN
                    return False;
                END IF;

                return False;
            END IF;	

            IF (elem1.value[3] is Null) THEN
                IF (elem2.value[3] is null) THEN
                    return False;		
                END IF;

                IF elem1.value[2] > elem2.value[3] THEN
                    return False;
                ELSE
                    return False;
                END IF;	
            END IF;
        END IF;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = False) and (elem2.type = True) THEN
                IF elem1.value[2] is Null THEN
                        return True;
                END IF;

                FOR i IN 1..size2 LOOP
                    IF elem2.odd[i] = 1 THEN
                        IF elem1.value[2] is not Null THEN
                            IF elem1.value[2] <= elem2.value[i] THEN
                                return True;
                            END IF;
                        END IF;
                    END IF;
                END LOOP;

                return False;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = True) and (elem2.type = False) THEN
            IF elem2.value[3] is Null THEN
                return True;
            END IF;

            FOR i IN 1..size1 LOOP
                IF elem1.odd[i] = 1 THEN
                    IF elem2.value[2] is not Null THEN
                        IF elem1.value[i] <= elem2.value[3] THEN
                            return True;
                        END IF;
                    END IF;
                END IF;
            END LOOP;

            return False;
        END IF;
    END;
$$ LANGUAGE plpgsql;

-- Definition of lower or equal element '<='
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_choquet_lower_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        comp1 float := 0;
        comp2 float := 0;
        size1 int := array_length(elem1.value,1);
        size2 int := array_length(elem2.value,1);
    BEGIN
        -- Both numbers are expressed by extension
        IF elem1.type and elem2.type THEN
            FOR j IN 0..size1 LOOP
                FOR i IN 0..size2 LOOP
                    IF elem2.value[i] > elem1.value[j] THEN
                        comp1 := comp1 + (elem2.odd[i] * elem1.odd[j]);
                    END IF;
                END LOOP;
            END LOOP;

            size1 = size2;
            size2 = array_length(elem1.value,1);

            FOR j IN 0..size1 LOOP
                FOR i IN 0..size2 LOOP
                    IF elem1.value[i] > elem2.value[j] THEN
                        comp2 := comp2 + (elem1.odd[i] * elem2.odd[j]);
                    END IF;
                END LOOP;
            END LOOP;

            return comp1 >= comp2;

        -- Both numbers are expressed by trapezoids
        ELSE IF (elem1.type = False) and (elem2.type = False) THEN
            IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
                IF (elem1.value[2] is not Null) THEN
                    IF elem1.value[2] > elem2.value[3] THEN
                        return False;
                    END IF;

                    return True;			
                END IF;
            END IF;

            IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
                IF elem1.value[3] < elem2.value[2] THEN
                    return True;		
                END IF;

                IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
                    return True;
                END IF;

                IF elem1.value[2] > elem2.value[3]  THEN
                    return False;
                END IF;

                return True;
            END IF;	

            IF (elem1.value[3] is Null) THEN
                IF (elem2.value[3] is null) THEN
                    return True;		
                END IF;

                IF elem1.value[2] > elem2.value[3] THEN
                    return False;
                ELSE
                    return True;
                END IF;	
            END IF;
        END IF;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = False) and (elem2.type = True) THEN
            IF elem1.value[2] is Null THEN
                return True;
            END IF;

            FOR i IN 1..size2 LOOP
                IF elem2.odd[i] = 1 THEN
                    IF elem1.value[2] is not Null THEN
                        IF elem1.value[2] <= elem2.value[i] THEN
                            return True;
                        END IF;
                    END IF;
                END IF;
            END LOOP;

            return False;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = True) and (elem2.type = False) THEN
            IF elem2.value[3] is Null THEN
                return True;
            END IF;

            FOR i IN 1..size1 LOOP
                IF elem1.odd[i] = 1 THEN
                    IF elem2.value[2] is not Null THEN
                        IF elem1.value[i] <= elem2.value[3] THEN
                            return True;
                        END IF;
                    END IF;
                END IF;
            END LOOP;

            return False;
        END IF;
    END;
$$ LANGUAGE plpgsql;

-- Definition of equal element '='
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_choquet_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        comp1 float := 0;
        comp2 float := 0;
        size1 int := array_length(elem1.value,1);
        size2 int := array_length(elem2.value,1);
        bool1 boolean;
        bool2 boolean;
    BEGIN
        -- Both numbers are expressed by extension
        IF elem1.type and elem2.type THEN
            FOR j IN 0..size1 LOOP
                FOR i IN 0..size2 LOOP
                    IF elem2.value[i] > elem1.value[j] THEN
                    
                        comp1 := comp1 + (elem2.odd[i] * elem1.odd[j]);
                    END IF;
                END LOOP;
            END LOOP;

            size1 = size2;
            size2 = array_length(elem1.value,1);

            FOR j IN 0..size1 LOOP
                FOR i IN 0..size2 LOOP
                    IF elem1.value[i] > elem2.value[j] THEN
                        comp2 := comp2 + (elem1.odd[i] * elem2.odd[j]);
                    END IF;
                END LOOP;
            END LOOP;

            return comp2 = comp1;

        -- Both numbers are expressed by trapezoids
        ELSE IF (elem1.type = False) and (elem2.type = False) THEN
            IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
                IF (elem1.value[2] is not Null) THEN
                    IF elem1.value[2] > elem2.value[3] THEN
                        return False;
                    END IF;

                    return True;			
                END IF;
            END IF;

            IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
                IF elem1.value[3] < elem2.value[2] THEN
                    return False;		
                END IF;

                IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
                    return True;
                END IF;

                IF elem1.value[2] > elem2.value[3]  THEN
                    return False;
                END IF;

                return True;
            END IF;	

            IF (elem1.value[3] is Null) THEN
                IF (elem2.value[3] is null) THEN
                    return True;		
                END IF;

                IF elem1.value[2] > elem2.value[3] THEN
                    return False;
                ELSE
                    return True;
                END IF;	
            END IF;
        END IF;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = False) and (elem2.type = True) THEN
            bool1 := False;
            bool2 := False;

            IF elem1.value[2] is Null THEN
                bool1 := True;
            END IF;

            IF elem1.value[3] is Null THEN
                bool2 := True;
            END IF;

            FOR i IN 1..size2 LOOP
                IF elem2.odd[i] = 1 THEN
                    IF elem1.value[2] is not Null THEN
                        IF elem1.value[2] <= elem2.value[i] THEN
                            bool1 := True;
                        END IF;
                    END IF;

                    IF elem1.value[3] is not Null THEN
                        IF elem1.value[3] >= elem2.value[i] THEN
                            bool2 := True;
                        END IF;
                    END IF;
                END IF;

                IF bool1 and bool2 THEN
                    return True;
                END IF;
            END LOOP;

            return False;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = True) and (elem2.type = False) THEN
            bool1 := False;
            bool2 := False;

            IF elem2.value[2] is Null THEN
                bool1 := True;
            END IF;

            IF elem2.value[3] is Null THEN
                bool2 := True;
            END IF;

            FOR i IN 1..size1 LOOP
                IF elem1.odd[i] = 1 THEN
                    IF elem2.value[2] is not Null THEN
                        IF elem2.value[2] <= elem1.value[i] THEN
                            bool1 := True;
                        END IF;
                    END IF;

                    IF elem2.value[3] is not Null THEN
                        IF elem2.value[3] >= elem1.value[i] THEN
                            bool2 := True;
                        END IF;
                    END IF;
                END IF;

                IF bool1 and bool2 THEN
                    return True;
                END IF;
            END LOOP;

            return False;
        END IF;
    END;
$$ LANGUAGE plpgsql;

-- Definition of greater element '>'
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_choquet_greater(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        comp1 float := 0;
        comp2 float := 0;
        size1 int := array_length(elem1.value,1);
        size2 int := array_length(elem2.value,1);
    BEGIN
        -- Both numbers are expressed by extension
        IF elem1.type and elem2.type THEN
            FOR j IN 0..size1 LOOP
                FOR i IN 0..size2 LOOP
                    IF elem2.value[i] > elem1.value[j] THEN
                        comp1 := comp1 + (elem2.odd[i] * elem1.odd[j]);
                    END IF;
                END LOOP;
            END LOOP;

            size1 = size2;
            size2 = array_length(elem1.value,1);

            FOR j IN 0..size1 LOOP
                FOR i IN 0..size2 LOOP
                    IF elem1.value[i] > elem2.value[j] THEN
                        comp2 := comp2 + (elem1.odd[i] > elem2.odd[j]);
                    END IF;
                END LOOP;
            END LOOP;

            return comp2 > comp1;

        -- Both numbers are expressed by trapezoids
        ELSE IF (elem1.type = False) and (elem2.type = False) THEN
            IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
                IF (elem1.value[2] is not Null) THEN
                    IF elem1.value[2] > elem2.value[3] THEN
                        return True;
                    END IF;

                    return False;			
                END IF;
            END IF;

            IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
                IF elem1.value[3] < elem2.value[2] THEN
                    return False;		
                END IF;

                IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
                    return False;
                END IF;

                IF elem1.value[2] > elem2.value[3]  THEN
                    return True;
                END IF;

                return False;
            END IF;	

            IF (elem1.value[3] is Null) THEN
                IF (elem2.value[3] is null) THEN
                    return False;		
                END IF;

                IF elem1.value[2] > elem2.value[3] THEN
                    return True;
                ELSE
                    return False;
                END IF;	
            END IF;
        END IF;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = False) and (elem2.type = True) THEN
            IF elem1.value[3] is Null THEN
                return True;
            END IF;

            FOR i IN 1..size2 LOOP
                IF elem2.odd[i] = 1 THEN
                    IF elem1.value[3] is not Null THEN
                        IF elem1.value[3] >= elem2.value[i] THEN
                            return True;
                        END IF;
                    END IF;
                END IF;
            END LOOP;

            return False;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = True) and (elem2.type = False) THEN
            IF elem2.value[2] is Null THEN
                return True;
            END IF;

            FOR i IN 1..size1 LOOP
                IF elem1.odd[i] = 1 THEN
                    IF elem2.value[2] is not Null THEN
                        IF elem2.value[2] <= elem1.value[i] THEN
                            return True;
                        END IF;
                    END IF;
                END IF;
            END LOOP;

            return False;
        END IF;
    END;
$$ LANGUAGE plpgsql;

-- Definition of greater or equal element '>'
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_choquet_greater_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        comp1 float := 0;
        comp2 float := 0;
        size1 int := array_length(elem1.value,1);
        size2 int := array_length(elem2.value,1);
    BEGIN
        -- Both numbers are expressed by extension
        IF elem1.type and elem2.type THEN
            FOR j IN 0..size1 LOOP
                FOR i IN 0..size2 LOOP
                    IF elem2.value[i] > elem1.value[j] THEN
                        comp1 := comp1 + (elem2.odd[i] * elem1.odd[j]);
                    END IF;
                END LOOP;
            END LOOP;

            size1 = size2;
            size2 = array_length(elem1.value,1);

            FOR j IN 0..size1 LOOP
                FOR i IN 0..size2 LOOP
                    IF elem1.value[i] > elem2.value[j] THEN
                        comp2 := comp2 + (elem1.odd[i] * elem2.odd[j]);
                    END IF;
                END LOOP;
            END LOOP;

            return comp2 >= comp1;

        -- Both numbers are expressed by trapezoids
        ELSE IF (elem1.type = False) and (elem2.type = False) THEN
            IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
                IF (elem1.value[2] is not Null) THEN
                    IF elem1.value[2] > elem2.value[3] THEN
                        return True;
                    END IF;
                        return True;			
                END IF;
            END IF;

            IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
                IF elem1.value[3] < elem2.value[2] THEN
                    return False;		
                END IF;

                IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
                    return True;
                END IF;

                IF elem1.value[2] > elem2.value[3]  THEN
                    return True;
                END IF;

                return True;
            END IF;	

            IF (elem1.value[3] is Null) THEN
                IF (elem2.value[3] is null) THEN
                    return True;		
                END IF;

                IF elem1.value[2] > elem2.value[3] THEN
                    return True;
                ELSE
                    return True;
                END IF;	
            END IF;
        END IF;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = False) and (elem2.type = True) THEN
            IF elem1.value[3] is Null THEN
                return True;
            END IF;

            FOR i IN 1..size2 LOOP
                IF elem2.odd[i] = 1 THEN
                    IF elem1.value[3] is not Null THEN
                        IF elem1.value[3] >= elem2.value[i] THEN
                            return True;
                        END IF;
                    END IF;
                END IF;
            END LOOP;

            return False;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = True) and (elem2.type = False) THEN
            IF elem2.value[2] is Null THEN
                return True;
            END IF;

            FOR i IN 1..size1 LOOP
                IF elem1.odd[i] = 1 THEN
                    IF elem2.value[2] is not Null THEN
                        IF elem2.value[2] <= elem1.value[i] THEN
                            return True;
                        END IF;
                    END IF;
                END IF;
            END LOOP;

            return False;
        END IF;
    END;
$$ LANGUAGE plpgsql;