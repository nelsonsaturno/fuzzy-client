#!/bin/bash
cd JSqlParser && \
	ant parser jar && \
	cd .. && mv JSqlParser/dist/QbosSqlParser.jar querying-based-on-similarity/libraries && \
	cd querying-based-on-similarity && ant jar
