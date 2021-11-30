.PHONY: clean all
all : bin/import-tool

bin/import-tool : src/import-tool.cc
	mkdir -p bin
	g++ -o $@ $?
	chmod u=swrx,g=srx,o=rx $@

clean :
	rm -f bin/import-tool
