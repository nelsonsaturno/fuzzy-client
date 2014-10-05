-- File: centroid-ordering.sql
-- Description: This file contains the stored functions for comparing two
--              fuzzy type II values based on the Riemman integral and the centroid. 

-- Definition of lower element '<'
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_lower(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        mass_moment_1   float := 0;
        mass_moment_2   float := 0;
        mass_1          float := 0;
        mass_2          float := 0;
        abscissa_1      float := 0;
        abscissa_2      float := 0;
        size1           int := array_length(elem1.value,1);
        size2           int := array_length(elem2.value,1);
    BEGIN
        -- Both numbers are expressed by extension
        IF elem1.type and elem2.type THEN
            -- Traverse elem1 to calculate its mass moment and its mass
            FOR j IN 0..size1 LOOP
                mass_moment_1   := mass_moment_1 + (elem1.value[j] * elem1.odd[j]);
                mass_1          := mass_1 + elem1.odd[j];
            END LOOP;
            -- Traverse elem2 to calculate its mass moment and its mass
            FOR i IN 0..size2 LOOP
                mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                mass_2          := mass_2 + elem2.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_1 := mass_moment_1::float / mass_1;
            abscissa_2 := mass_moment_2::float / mass_2;
            RETURN abscissa_2 > abscissa_1;

        -- Both numbers are expressed by trapezoids
        ELSE IF (elem1.type = FALSE) AND (elem2.type = FALSE) THEN
            -- elem1 is a right shoulder trapezoid type
            IF (elem1.value[2] IS NULL) THEN
                RETURN TRUE;
            END IF;
            -- elem2 is a left shoulder trapezoid type
            IF (elem2.value[3] IS NULL) THEN
                RETURN TRUE;
            END IF;
            -- elem2 is a right shoulder trapezoid type
            IF (elem2.value[2] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- elem2 is a left shoulder trapezoid type
            IF (elem1.value[3] IS NULL) THEN
                RETURN FALSE;
            END IF;   
            -- Calculte both the abscissas
            abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
            abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
            abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

            abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
            abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
            abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_2);

            RETURN abscissa_2 > abscissa_1;
        END IF;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = FALSE) AND (elem2.type = TRUE) THEN
                -- elem1 is a right shoulder trapezoid type
                IF (elem1.value[2] IS NULL) THEN
                    RETURN TRUE;
                END IF;
                -- Traverse elem2 to calculate its mass moment and its mass
                FOR i IN 1..size2 LOOP
                    mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                    mass_2          := mass_2 + elem2.odd[i];
                END LOOP;
                -- Calculte both the abscissas
                abscissa_2 := mass_moment_2::float / mass_2;

                abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
                abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
                abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

                RETURN abscissa_2 > abscissa_1;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = TRUE) AND (elem2.type = FALSE) THEN
            -- elem2 is a left shoulder trapezoid type
            IF (elem2.value[3] IS NULL) THEN
                RETURN TRUE;
            END IF;
            -- Traverse elem1 to calculate its mass moment and its mass
            FOR i IN 1..size1 LOOP
                mass_moment_1   := mass_moment_1 + (elem1.value[i] * elem1.odd[i]);
                mass_1          := mass_1 + elem1.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_1 := mass_moment_1::float / mass_1;

            abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
            abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
            abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_1);

            RETURN abscissa_2 > abscissa_1;
        END IF;
    END;
$$ LANGUAGE plpgsql;

-- Definition of lower or equal element '<='
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_lower_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        mass_moment_1   float := 0;
        mass_moment_2   float := 0;
        mass_1          float := 0;
        mass_2          float := 0;
        abscissa_1      float := 0;
        abscissa_2      float := 0;
        size1           int := array_length(elem1.value,1);
        size2           int := array_length(elem2.value,1);
    BEGIN
        -- Both numbers are expressed by extension
        IF elem1.type and elem2.type THEN
            -- Traverse elem1 to calculate its mass moment and its mass
            FOR j IN 0..size1 LOOP
                mass_moment_1   := mass_moment_1 + (elem1.value[j] * elem1.odd[j]);
                mass_1          := mass_1 + elem1.odd[j];
            END LOOP;
            -- Traverse elem2 to calculate its mass moment and its mass
            FOR i IN 0..size2 LOOP
                mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                mass_2          := mass_2 + elem2.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_1 := mass_moment_1::float / mass_1;
            abscissa_2 := mass_moment_2::float / mass_2;
            RETURN abscissa_2 >= abscissa_1;

        -- Both numbers are expressed by trapezoids
        ELSE IF (elem1.type = FALSE) AND (elem2.type = FALSE) THEN
            -- elem1 is a right shoulder trapezoid type
            IF (elem1.value[2] IS NULL) THEN
                RETURN TRUE;
            END IF;
            -- elem2 is a left shoulder trapezoid type
            IF (elem2.value[3] IS NULL) THEN
                RETURN TRUE;
            END IF;
            -- elem2 is a right shoulder trapezoid type
            IF (elem2.value[2] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- elem1 is a left shoulder trapezoid type
            IF (elem1.value[3] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- Calculte both the abscissas
            abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
            abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
            abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

            abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
            abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
            abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_2);

            RETURN abscissa_2 >= abscissa_1;
        END IF;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = FALSE) AND (elem2.type = TRUE) THEN
            -- elem1 is a left shoulder trapezoid type
            IF (elem1.value[2] IS NULL) THEN
                RETURN TRUE;
            END IF;
            -- Traverse elem2 to calculate its mass moment and its mass
            FOR i IN 1..size2 LOOP
                mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                mass_2          := mass_2 + elem2.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_2 := mass_moment_2::float / mass_2;

            abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
            abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
            abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

            RETURN abscissa_2 >= abscissa_1;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = TRUE) AND (elem2.type = FALSE) THEN
            -- elem2 is a left shoulder trapezoid type
            IF (elem2.value[3] IS NULL) THEN
                RETURN TRUE;
            END IF;
            -- Traverse elem1 to calculate its mass moment and its mass
            FOR i IN 1..size1 LOOP
                mass_moment_1   := mass_moment_1 + (elem1.value[i] * elem1.odd[i]);
                mass_1          := mass_1 + elem1.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_1 := mass_moment_1::float / mass_1;

            abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
            abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
            abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_1);

            RETURN abscissa_2 >= abscissa_1;
        END IF;
    END;
$$ LANGUAGE plpgsql;

-- Definition of equal element '='
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        mass_moment_1   float := 0;
        mass_moment_2   float := 0;
        mass_1          float := 0;
        mass_2          float := 0;
        abscissa_1      float := 0;
        abscissa_2      float := 0;
        size1           int := array_length(elem1.value,1);
        size2           int := array_length(elem2.value,1);
    BEGIN
        -- Both numbers are expressed by extension
        IF elem1.type and elem2.type THEN
            -- Traverse elem1 to calculate its mass moment and its mass
            FOR j IN 0..size1 LOOP
                mass_moment_1   := mass_moment_1 + (elem1.value[j] * elem1.odd[j]);
                mass_1          := mass_1 + elem1.odd[j];
            END LOOP;
            -- Traverse elem2 to calculate its mass moment and its mass
            FOR i IN 0..size2 LOOP
                mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                mass_2          := mass_2 + elem2.odd[i];
            END LOOP;
            -- Calculate both the abscissas
            abscissa_1 := mass_moment_1::float / mass_1;
            abscissa_2 := mass_moment_2::float / mass_2;
            RETURN abscissa_1 = abscissa_2;

        -- Both numbers are expressed by trapezoids
        ELSE IF (elem1.type = FALSE) AND (elem2.type = FALSE) THEN
            -- elem1 is a right shoulder trapezoid type
            IF (elem1.value[2] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- elem2 is a left shoulder trapezoid type
            IF (elem2.value[3] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- elem2 is a right shoulder trapezoid type
            IF (elem2.value[2] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- elem1 is a left shoulder trapezoid type
            IF (elem1.value[3] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- Calculte both the abscissas
            abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
            abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
            abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

            abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
            abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
            abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_2);

            RETURN abscissa_1 = abscissa_2;
        END IF;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = FALSE) AND (elem2.type = TRUE) THEN
            -- elem1 is a right shoulder trapezoid type
            IF (elem1.value[2] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- Traverse elem2 to calculate its mass moment and its mass
            FOR i IN 1..size2 LOOP
                mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                mass_2          := mass_2 + elem2.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_2 := mass_moment_2::float / mass_2;

            abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
            abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
            abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

            RETURN abscissa_1::float = abscissa_2;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = TRUE) AND (elem2.type = FALSE) THEN
            -- elem2 is a left shoulder trapezoid type
            IF (elem2.value[3] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- Traverse elem1 to calculate its mass moment and its mass
            FOR i IN 1..size1 LOOP
                mass_moment_1   := mass_moment_1 + (elem1.value[i] * elem1.odd[i]);
                mass_1          := mass_1 + elem1.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_1 := mass_moment_1::float / mass_1;

            abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
            abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
            abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_1);

            RETURN abscissa_1 = abscissa_2;
        END IF;
    END;
$$ LANGUAGE plpgsql;

-- Definition of greater element '>'
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_greater(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        mass_moment_1   float := 0;
        mass_moment_2   float := 0;
        mass_1          float := 0;
        mass_2          float := 0;
        abscissa_1      float := 0;
        abscissa_2      float := 0;
        size1           int := array_length(elem1.value,1);
        size2           int := array_length(elem2.value,1);
    BEGIN
        -- Both numbers are expressed by extension
        IF elem1.type and elem2.type THEN
            -- Traverse elem1 to calculate its mass moment and its mass
            FOR j IN 0..size1 LOOP
                mass_moment_1   := mass_moment_1 + (elem1.value[j] * elem1.odd[j]);
                mass_1          := mass_1 + elem1.odd[j];
            END LOOP;
            -- Traverse elem2 to calculate its mass moment and its mass
            FOR i IN 0..size2 LOOP
                mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                mass_2          := mass_2 + elem2.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_1 := mass_moment_1::float / mass_1;
            abscissa_2 := mass_moment_2::float / mass_2;
            RETURN abscissa_1 > abscissa_2;

        -- Both numbers are expressed by trapezoids
        ELSE IF (elem1.type = FALSE) AND (elem2.type = FALSE) THEN
            -- elem2 is a right shoulder trapezoid type
            IF (elem2.value[2] IS NULL) THEN
                RETURN TRUE;
            END IF;
            -- elem1 is a left shoulder trapezoid type
            IF (elem1.value[3] IS NULL) THEN
                RETURN TRUE;
            END IF;
            -- elem1 is a right shoulder trapezoid type
            IF (elem1.value[2] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- elem2 is a left shoulder trapezoid type
            IF (elem2.value[3] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- Calculte both the abscissas
            abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
            abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
            abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

            abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
            abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
            abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_2);

            RETURN abscissa_1 > abscissa_2;
        END IF;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = FALSE) AND (elem2.type = TRUE) THEN
            -- elem1 is a right shoulder trapezoid type
            IF (elem1.value[2] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- Traverse elem2 to calculate its mass moment and its mass
            FOR i IN 1..size2 LOOP
                mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                mass_2          := mass_2 + elem2.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_2 := mass_moment_2::float / mass_2;

            abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
            abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
            abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

            RETURN abscissa_1 > abscissa_2;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = TRUE) AND (elem2.type = FALSE) THEN
            -- elem2 is a left shoulder trapezoid type
            IF (elem2.value[3] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- Traverse elem1 to calculate its mass moment and its mass
            FOR i IN 1..size1 LOOP
                mass_moment_1   := mass_moment_1 + (elem1.value[i] * elem1.odd[i]);
                mass_1          := mass_1 + elem1.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_1 := mass_moment_1::float / mass_1;

            abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
            abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
            abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_1);

            RETURN abscissa_1 > abscissa_2;
        END IF;
    END;
$$ LANGUAGE plpgsql;

-- Definition of greater or equal element '>'
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_greater_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
    DECLARE
        mass_moment_1   float := 0;
        mass_moment_2   float := 0;
        mass_1          float := 0;
        mass_2          float := 0;
        abscissa_1      float := 0;
        abscissa_2      float := 0;
        size1           int := array_length(elem1.value,1);
        size2           int := array_length(elem2.value,1);
    BEGIN
        -- Both numbers are expressed by extension
        IF elem1.type and elem2.type THEN
            -- Traverse elem1 to calculate its mass moment and its mass
            FOR j IN 0..size1 LOOP
                mass_moment_1   := mass_moment_1 + (elem1.value[j] * elem1.odd[j]);
                mass_1          := mass_1 + elem1.odd[j];
            END LOOP;
            -- Traverse elem2 to calculate its mass moment and its mass
            FOR i IN 0..size2 LOOP
                mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                mass_2          := mass_2 + elem2.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_1 := mass_moment_1::float / mass_1;
            abscissa_2 := mass_moment_2::float / mass_2;
            RETURN abscissa_1 >= abscissa_2;

        -- Both numbers are expressed by trapezoids
        ELSE IF (elem1.type = FALSE) AND (elem2.type = FALSE) THEN
            -- elem2 is a right shoulder trapezoid type
            IF (elem2.value[2] IS NULL) THEN
                RETURN TRUE;
            END IF;
            -- elem1 is a left shoulder trapezoid type
            IF (elem1.value[3] IS NULL) THEN
                RETURN TRUE;
            END IF;
            -- elem1 is a right shoulder trapezoid type
            IF (elem1.value[2] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- elem2 is a left shoulder trapezoid type
            IF (elem2.value[3] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- Calculte both the abscissas
            abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
            abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
            abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

            abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
            abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
            abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_2);

            RETURN abscissa_1 >= abscissa_2;
        END IF;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = FALSE) AND (elem2.type = TRUE) THEN
            -- elem1 is a right shoulder trapezoid type
            IF (elem1.value[2] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- Traverse elem2 to calculate its mass moment and its mass
            FOR i IN 1..size2 LOOP
                mass_moment_2   := mass_moment_2 + (elem2.value[i] * elem2.odd[i]);
                mass_2          := mass_2 + elem2.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_2 := mass_moment_2::float / mass_2;

            abscissa_1 := (elem1.value[1] * elem1.value[2]) + (elem1.value[3] * elem1.value[4]);
            abscissa_1 := abscissa_1::float / (elem1.value[4] - elem1.value[1] + elem1.value[3] - elem1.value[2]);
            abscissa_1 := (1::float/3) * (elem1.value[1] + elem1.value[2] + elem1.value[3] + elem1.value[4] + abscissa_1);

            RETURN abscissa_1 >= abscissa_2;
        END IF;

        -- One number is a trapezoid and the other one is by extension
        IF (elem1.type = TRUE) AND (elem2.type = FALSE) THEN
            -- elem2 is a left shoulder trapezoid type
            IF (elem2.value[3] IS NULL) THEN
                RETURN FALSE;
            END IF;
            -- Traverse elem1 to calculate its mass moment and its mass
            FOR i IN 1..size1 LOOP
                mass_moment_1   := mass_moment_1 + (elem1.value[i] * elem1.odd[i]);
                mass_1          := mass_1 + elem1.odd[i];
            END LOOP;
            -- Calculte both the abscissas
            abscissa_1 := mass_moment_1::float / mass_1;

            abscissa_2 := (elem2.value[1] * elem2.value[2]) + (elem2.value[3] * elem2.value[4]);
            abscissa_2 := abscissa_2::float / (elem2.value[4] - elem2.value[1] + elem2.value[3] - elem2.value[2]);
            abscissa_2 := (1::float/3) * (elem2.value[1] + elem2.value[2] + elem2.value[3] + elem2.value[4] + abscissa_1);

            RETURN abscissa_1 >= abscissa_2;
        END IF;
    END;
$$ LANGUAGE plpgsql;