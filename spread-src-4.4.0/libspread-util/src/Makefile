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

LIBFULLVERSION=4.2.0
LIBCOMPATVERSION=4.0
LIBVERSION=4

PATHS=-I. -I$(top_srcdir)/src -I../include -I$(top_srcdir)/include

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

TARGETS=libspread-util.a libspread-util.sa libspread-util.dylib

LIB_OBJS=alarm.o events.o memory.o data_link.o

LIB_SHOBJS=$(LIB_OBJS:.o=.lo)

all: $(TARGETS) binrelease

.c.o:
	$(CC) $(CFLAGS) $(CPPFLAGS) -D_REENTRANT -c $<

.c.lo:
	$(SHCC) $(SHCFLAGS) $(SHCPPFLAGS) -D_REENTRANT -c $< -o $*.lo

libspread-util.a: $(LIB_OBJS)
	$(AR) rv $@ $(LIB_OBJS)
	$(RANLIB) $@

libspread-util.sa: $(LIB_SHOBJS)
	$(AR) rv $@ $(LIB_SHOBJS)
	$(RANLIB) $@

libspread-util.dylib:	$(LIB_SHOBJS)
	$(SHLD) -o libspread-util.$(LIBFULLVERSION).dylib $(LIB_SHOBJS) $(SHLDFLAGS) $(SHLIBS) $(SHLDNAME)libspread-util.$(LIBVERSION).dylib  -current_version $(LIBFULLVERSION) -compatibility_version $(LIBCOMPATVERSION)
	$(SOFTLINK) -f libspread-util.$(LIBFULLVERSION).dylib $@

install-libspread-util.dylib:	libspread-util.dylib
	$(INSTALL) -m 0755 libspread-util.$(LIBFULLVERSION).dylib $(DESTDIR)$(libdir)/libspread-util.$(LIBFULLVERSION).dylib
	(cd $(DESTDIR)$(libdir); \
		$(SOFTLINK) -f libspread-util.$(LIBFULLVERSION).dylib libspread-util.dylib; \
		$(SOFTLINK) -f libspread-util.$(LIBFULLVERSION).dylib libspread-util.$(LIBVERSION).dylib; \
		install_name_tool -id $(DESTDIR)$(libdir)/libspread-util.$(LIBVERSION).dylib $(DESTDIR)$(libdir)/libspread-util.$(LIBFULLVERSION).dylib \
	)

libspread-util.so:	$(LIB_SHOBJS)
	$(SHLD) -o libspread-util.so.$(LIBFULLVERSION) $(LIB_SHOBJS) $(SHLDFLAGS) $(SHLIBS) $(SHLDOPTION)$(SHLDNAME)$@.$(LIBVERSION)
	$(SOFTLINK) -f libspread-util.so.$(LIBFULLVERSION) $@

install-libspread-util.so:	libspread-util.so
	$(INSTALL) -m 0755 libspread-util.so $(DESTDIR)$(libdir)/libspread-util.so.$(LIBFULLVERSION)
	(cd $(DESTDIR)$(libdir); \
		$(SOFTLINK) -f libspread-util.so.$(LIBFULLVERSION) libspread-util.so; \
		$(SOFTLINK) -f libspread-util.so.$(LIBFULLVERSION) libspread-util.so.$(LIBVERSION) \
	)

clean:
	rm -f *.lo *.o *.a *.sa *.dylib *.so $(TARGETS)
	rm -f core core.*
	rm -rf autom4te.cache
	rm -rf ../lib/$(host)

distclean: clean
	rm -f Makefile config.h config.status *~

binrelease: $(TARGETS)
	$(buildtoolsdir)/mkinstalldirs ../lib/$(host)
	$(INSTALL) -m 644 libspread-util.a ../lib/$(host)/libspread-util.a
	$(INSTALL) -m 644 libspread-util.a ../lib/libspread-util.a
	$(INSTALL) -m 644 libspread-util.sa ../lib/$(host)/libspread-util.sa
	$(INSTALL) -m 644 libspread-util.sa ../lib/libspread-util.sa
ifeq (libspread-util.dylib, libspread-util.so)
	  $(INSTALL) -m 755 libspread-util.so.$(LIBFULLVERSION) ../lib/$(host)/libspread-util.so.$(LIBFULLVERSION)
	  $(INSTALL) -m 755 libspread-util.so.$(LIBFULLVERSION) ../lib/libspread-util.so.$(LIBFULLVERSION)
endif
ifeq (libspread-util.dylib, libspread-util.dylib)
	  $(INSTALL) -m 755 libspread-util.$(LIBFULLVERSION).dylib ../lib/$(host)/libspread-util.$(LIBFULLVERSION).dylib
	  $(INSTALL) -m 755 libspread-util.$(LIBFULLVERSION).dylib ../lib/libspread-util.$(LIBFULLVERSION).dylib
endif


install: $(TARGETS) install-files install-libspread-util.dylib

install-files:
	$(buildtoolsdir)/mkinstalldirs $(DESTDIR)$(libdir)
	$(INSTALL) -m 644 libspread-util.a $(DESTDIR)$(libdir)/libspread-util.a

uninstall-libspread-util.dylib:	
	-rm -f $(DESTDIR)$(libdir)/libspread-util.$(LIBFULLVERSION).dylib 
	-rm -f $(DESTDIR)$(libdir)/libspread-util.dylib
	-rm -f $(DESTDIR)$(libdir)/libspread-util.$(LIBVERSION).dylib

uninstall-libspread-util.so:
	-rm -f $(DESTDIR)$(libdir)/libspread-util.so.$(LIBFULLVERSION)
	-rm -f $(DESTDIR)$(libdir)/libspread-util.so
	-rm -f $(DESTDIR)$(libdir)/libspread-util.so.$(LIBVERSION)

uninstall: uninstall-libspread-util.dylib
	-rm -f $(DESTDIR)$(libdir)/libspread-util.a

uninstallall:	uninstall
	-rmdir $(DESTDIR)$(libdir)
