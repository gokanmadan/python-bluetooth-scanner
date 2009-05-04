all:
	cd btscan && make
	
clean:
	cd btscan && make clean
	cd btscan && rm *.pyc || true
	
run: all
	python scanner.py
