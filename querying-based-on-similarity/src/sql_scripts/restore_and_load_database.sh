#!/bin/bash
mysql -u root < drop-schema.sql
mysql -u root < test/drop-schema.sql
mysql -u root < create-schema.sql
mysql -u root < test/create-schema.sql
mysql -u root < test/load-data.sql
