.SUFFIXES: .lo

prefix=/usr/local
exec_prefix=${prefix}
bindir=${exec_prefix}/bin
sbindir=${exec_prefix}/sbin
datarootdir=${prefix}/share
libdir=${exec_prefix}/lib
includedir=${prefix}/include
mandir=${datarootdir}/man
mansubdir=man
sysconfdir=${prefix}/etc
srcdir=.
top_srcdir=..
buildtoolsdir=$(top_srcdir)/buildtools
host=i386-apple-darwin17.5.0

DESTDIR=
VPATH= $(top_srcdir)/src

#Shared Library version -- Must be changed upon each release 
#Rules: major  -- inc for incompatible change
#     : minor  -- inc for backwards compatible change (add new api, but not break or remove old one)
#     : bugfix -- inc for bugfix that is fully compatible

LIBFULLVERSION=0.0.1
LIBCOMPATVERSION=0.0
LIBVERSION=0

PATHS=-I. -I$(top_srcdir)/src

CC=gcc
LD=gcc

# don't use debugging (asserts and dprintfs) (optional)
NO_DEBUG=-DNDEBUG
DEFINES=$(NO_DEBUG)

CFLAGS=-g -O2 -Wall $(DEFINES)
CPPFLAGS= $(PATHS) -DHAVE_CONFIG_H
LDFLAGS= 
LIBS=-lm  
THLDFLAGS=
THLIBS=-lpthread 
AR=/usr/bin/ar
SHCC=gcc
SHLD=gcc -dynamiclib
SHCFLAGS=-g -O2 -Wall -fPIC -fno-common $(DEFINES)
SHCPPFLAGS= $(PATHS) -DHAVE_CONFIG_H
SHLDFLAGS= 
SHLIBS=-lm 
SHLDOPTION=-Wl,
SHLDNAME=-headerpad_max_install_names -install_name 
SHLDCONVERTSTATIC=
SHLDCONVERTSTATICEND=
RANLIB=ranlib
INSTALL=/usr/local/bin/ginstall -c
SOFTLINK=ln -s
PERL=/usr/bin/perl
ENT=@ENT@
EXEEXT=

TARGETS=spu_system.h

HEADER_FILES=spu_alarm.h spu_alarm_types.h spu_data_link.h spu_events.h spu_memory.h spu_objects.h spu_objects_local.h spu_scatter.h spu_system_defs.h spu_system_defs_autoconf.h spu_system_defs_windows.h

all: $(TARGETS)

spu_system.h: ../src/config.h
	$(buildtoolsdir)/gen_system_include.sh ../src/config.h > spu_system.h

install-headers: spu_system.h
	$(buildtoolsdir)/mkinstalldirs $(DESTDIR)$(includedir)
	for incfile in $(HEADER_FILES); \
	do \
	  $(INSTALL) -m 644 $(srcdir)/$$incfile $(DESTDIR)$(includedir)/$$incfile; \
	done
	$(INSTALL) -m 644 spu_system.h $(DESTDIR)$(includedir)/spu_system.h

clean:
	rm -f spu_system.h

distclean: clean
	rm -f Makefile *~

install: $(TARGETS) install-headers

uninstall: 
	for incfile in $(HEADER_FILES); \
	do \
	  rm -f $(DESTDIR)$(includedir)/$$incfile; \
	done
	rm -f $(DESTDIR)$(includedir)/spu_system.h

uninstallall:	uninstall
	echo "Will not remove include directory"
