.SUFFIXES: .lo .to .tlo

prefix=/usr/local
exec_prefix=${prefix}
bindir=${exec_prefix}/bin
sbindir=${exec_prefix}/sbin
datarootdir=@datarootdir@
libdir=${exec_prefix}/lib
includedir=${prefix}/include
mandir=${prefix}/man
mansubdir=man
sysconfdir=${prefix}/etc
piddir=/var/run
srcdir=.
top_srcdir=..
buildtoolsdir=$(top_srcdir)/buildtools
host=i386-apple-darwin17.5.0

DESTDIR=
VPATH= $(top_srcdir)/daemon
SPREAD_PROGRAM=${exec_prefix}/sbin/spread
SP_MAJOR_VERSION=4
SP_MINOR_VERSION=4
SP_PATCH_VERSION=0
VERSION=4.4.0

#Shared Library version -- Must be changed upon each release 
#Rules: major  -- inc for incompatible change
#     : minor  -- inc for backwards compatible change (add new api, but not break or remove old one)
#     : bugfix -- inc for bugfix that is fully compatible

LIBFULLVERSION=3.0.0
LIBCOMPATVERSION=3.0
LIBVERSION=3

PATHS= 

CC=gcc
LD=gcc

# don't use debugging (asserts and dprintfs) (optional)
NO_DEBUG = -DNDEBUG
#flush defines
DEFINES= $(NO_DEBUG)

CFLAGS=-g -O2 -Wall $(DEFINES)
#libspread-util includes
LIBSPREADUTIL_DIR=../libspread-util
LIBSPREADUTIL_PATHS=-I$(LIBSPREADUTIL_DIR)/include -I$(top_srcdir)/libspread-util/include
#stdutil includes
FLUSH_INC_DIR = -I../stdutil/src -I$(top_srcdir)/stdutil/src -I$(srcdir) 
CPPFLAGS=-I$(top_srcdir)/daemon -I../daemon -I$(top_srcdir)/include $(FLUSH_INC_DIR) $(LIBSPREADUTIL_PATHS)  $(PATHS) -DHAVE_CONFIG_H
LDFLAGS= 
LIBS=-lm  
THLDFLAGS=
THLIBS=-lpthread 
AR=/usr/bin/ar
LEX=/usr/bin/flex
SHCC=gcc
SHLD=gcc -dynamiclib
SHCFLAGS=-g -O2 -Wall -fPIC -fno-common $(DEFINES)
SHCPPFLAGS= -I$(top_srcdir)/daemon -I../daemon -I$(top_srcdir)/include $(FLUSH_INC_DIR) $(LIBSPREADUTIL_PATHS)
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

SP_OBJ_DIR=../daemon

TARGETS=libspread-core.a libtspread-core.a libspread.a libspread.dylib libspread-core.dylib libtspread-core.dylib

LIBSP_OBJS= sp.o

LIBSP_SHOBJS= sp.lo

LIBTSP_OBJS= sp.to

LIBTSP_SHOBJS= sp.tlo

LIBTFL_OBJS= fl.to scatp.to sp.to

LIBTFL_SHOBJS= fl.tlo scatp.tlo sp.tlo

# A bit of a hack to grab the -fPIC threaded object files to merge into the shared spread library
# since the stdutil.so cannot be merged, and the stdutil.a is not compiled -fPIC
# Can be fixed correctly if stdutil generates incremental shared library versions
STDUTIL_DIR=../stdutil/src

SHARED_STDUTIL= $(STDUTIL_DIR)/stdarr.lto $(STDUTIL_DIR)/stdcarr.lto $(STDUTIL_DIR)/stddll.lto $(STDUTIL_DIR)/stderror.lto $(STDUTIL_DIR)/stdfd.lto $(STDUTIL_DIR)/stdhash.lto $(STDUTIL_DIR)/stdit.lto $(STDUTIL_DIR)/stdskl.lto $(STDUTIL_DIR)/stdthread.lto $(STDUTIL_DIR)/stdtime.lto $(STDUTIL_DIR)/stdutil.lto

all: $(TARGETS)

.c.o:
	$(CC) $(CFLAGS) $(CPPFLAGS) -c $<

.c.to:
	$(CC) $(CFLAGS) $(CPPFLAGS) -D_REENTRANT -c $< -o $*.to

.c.lo:
	$(SHCC) $(SHCFLAGS) $(SHCPPFLAGS) -c $< -o $*.lo

.c.tlo:
	$(SHCC) $(SHCFLAGS) $(SHCPPFLAGS) -D_REENTRANT -c $< -o $*.tlo

none:	
	@echo "Skipping shared library creation."

install-none:
	@echo "Skipping shared library installation."

# libspread-core is the core spread library without Flush or Stdutil
libspread-core.a: $(LIBSP_OBJS) $(LIBSPREADUTIL_DIR)/lib/libspread-util.a
	$(AR) rv $@ $(LIBSP_OBJS)
	$(AR) x $(LIBSPREADUTIL_DIR)/lib/libspread-util.a
	$(AR) rv $@ alarm.o data_link.o memory.o events.o
	$(RANLIB) $@

libspread-core.dylib:	$(LIBSP_SHOBJS) $(LIBSPREADUTIL_DIR)/lib/libspread-util.sa
	$(SHLD) -o libspread-core.$(LIBFULLVERSION).dylib $(LIBSP_SHOBJS) $(SHLDFLAGS) $(SHLDCONVERTSTATIC) $(LIBSPREADUTIL_DIR)/lib/libspread-util.sa $(SHLDCONVERTSTATICEND) $(SHLIBS) $(SHLDNAME)libspread-core.$(LIBVERSION).dylib  -current_version $(LIBFULLVERSION) -compatibility_version $(LIBCOMPATVERSION)
	$(SOFTLINK) -f libspread-core.$(LIBFULLVERSION).dylib $@

install-libspread-core.dylib:	libspread-core.dylib install-files
	$(INSTALL) -m 0755 libspread-core.$(LIBFULLVERSION).dylib $(DESTDIR)$(libdir)/libspread-core.$(LIBFULLVERSION).dylib
	(cd $(DESTDIR)$(libdir); \
		$(SOFTLINK) -f libspread-core.$(LIBFULLVERSION).dylib libspread-core.dylib; \
		$(SOFTLINK) -f libspread-core.$(LIBFULLVERSION).dylib libspread-core.$(LIBVERSION).dylib; \
		install_name_tool -id $(DESTDIR)$(libdir)/libspread-core.$(LIBVERSION).dylib $(DESTDIR)$(libdir)/libspread-core.$(LIBFULLVERSION).dylib \
	)

libspread-core.so:	$(LIBSP_SHOBJS) $(LIBSPREADUTIL_DIR)/lib/libspread-util.sa
	$(SHLD) -o $@ $(LIBSP_SHOBJS) $(SHLDFLAGS) $(SHLDCONVERTSTATIC) $(LIBSPREADUTIL_DIR)/lib/libspread-util.sa $(SHLDCONVERTSTATICEND) $(SHLIBS) $(SHLDOPTION)$(SHLDNAME)$@.$(LIBVERSION)

install-libspread-core.so:	libspread-core.so install-files
	$(INSTALL) -m 0755 libspread-core.so $(DESTDIR)$(libdir)/libspread-core.so.$(LIBFULLVERSION)
	(cd $(DESTDIR)$(libdir); \
		$(SOFTLINK) -f libspread-core.so.$(LIBFULLVERSION) libspread-core.so; \
		$(SOFTLINK) -f libspread-core.so.$(LIBFULLVERSION) libspread-core.so.$(LIBVERSION) \
	)

libtspread-core.a: $(LIBTSP_OBJS) $(LIBSPREADUTIL_DIR)/lib/libspread-util.a
	$(AR) rv $@ $(LIBTSP_OBJS)
	$(AR) x $(LIBSPREADUTIL_DIR)/lib/libspread-util.a
	$(AR) rv $@ alarm.o data_link.o memory.o events.o
	$(RANLIB) $@

libtspread-core.dylib:	$(LIBTSP_SHOBJS) $(LIBSPREADUTIL_DIR)/lib/libspread-util.sa
	$(SHLD) -o libtspread-core.$(LIBFULLVERSION).dylib $(LIBTSP_SHOBJS) $(SHLDFLAGS) $(SHLDCONVERTSTATIC) $(LIBSPREADUTIL_DIR)/lib/libspread-util.sa $(SHLDCONVERTSTATICEND) $(SHLIBS) $(THLIBS) $(SHLDNAME)libtspread-core.$(LIBVERSION).dylib  -current_version $(LIBFULLVERSION) -compatibility_version $(LIBCOMPATVERSION)
	$(SOFTLINK) -f libtspread-core.$(LIBFULLVERSION).dylib $@

install-libtspread-core.dylib:	libtspread-core.dylib install-files
	$(INSTALL) -m 0755 libtspread-core.$(LIBFULLVERSION).dylib $(DESTDIR)$(libdir)/libtspread-core.$(LIBFULLVERSION).dylib
	(cd $(DESTDIR)$(libdir); \
		$(SOFTLINK) -f libtspread-core.$(LIBFULLVERSION).dylib libtspread-core.dylib; \
		$(SOFTLINK) -f libtspread-core.$(LIBFULLVERSION).dylib libtspread-core.$(LIBVERSION).dylib; \
		install_name_tool -id $(DESTDIR)$(libdir)/libtspread-core.$(LIBVERSION).dylib $(DESTDIR)$(libdir)/libtspread-core.$(LIBFULLVERSION).dylib \
	)

libtspread-core.so:	$(LIBTSP_SHOBJS) $(LIBSPREADUTIL_DIR)/lib/libspread-util.sa
	$(SHLD) -o $@ $(LIBTSP_SHOBJS) $(SHLDFLAGS) $(SHLDCONVERTSTATIC) $(LIBSPREADUTIL_DIR)/lib/libspread-util.sa $(SHLDCONVERTSTATICEND) $(SHLIBS) $(THLIBS) $(SHLDOPTION)$(SHLDNAME)$@.$(LIBVERSION)

install-libtspread-core.so:	libtspread-core.so install-files
	$(INSTALL) -m 0755 libtspread-core.so $(DESTDIR)$(libdir)/libtspread-core.so.$(LIBFULLVERSION)
	(cd $(DESTDIR)$(libdir); \
		$(SOFTLINK) -f libtspread-core.so.$(LIBFULLVERSION) libtspread-core.so; \
		$(SOFTLINK) -f libtspread-core.so.$(LIBFULLVERSION) libtspread-core.so.$(LIBVERSION) \
	)

# Full Spread library with Fl_, SP_, and support modules linked in
libspread.a: $(LIBTFL_OBJS) ../stdutil/lib/libstdutil-threaded-release.a $(LIBSPREADUTIL_DIR)/lib/libspread-util.a
	$(AR) rv $@ $(LIBTFL_OBJS)
	$(AR) x ../stdutil/lib/libstdutil-threaded-release.a
	$(AR) rv $@ std*.to
	$(AR) x $(LIBSPREADUTIL_DIR)/lib/libspread-util.a
	$(AR) rv $@ alarm.o data_link.o memory.o events.o
	$(RANLIB) $@

libspread.dylib:	$(LIBTFL_SHOBJS) $(LIBSPREADUTIL_DIR)/lib/libspread-util.sa
	$(SHLD) -o libspread.$(LIBFULLVERSION).dylib $(LIBTFL_SHOBJS) $(SHLDFLAGS) $(SHLDCONVERTSTATIC) ../stdutil/lib/libstdutil-threaded-release.a $(LIBSPREADUTIL_DIR)/lib/libspread-util.sa $(SHLDCONVERTSTATICEND) $(SHLIBS) $(THLIBS) $(SHLDNAME)libspread.$(LIBVERSION).dylib  -current_version $(LIBFULLVERSION) -compatibility_version $(LIBCOMPATVERSION)
	$(SOFTLINK) -f libspread.$(LIBFULLVERSION).dylib $@

install-libspread.dylib:	libspread.dylib install-files
	$(INSTALL) -m 0755 libspread.$(LIBFULLVERSION).dylib $(DESTDIR)$(libdir)/libspread.$(LIBFULLVERSION).dylib
	(cd $(DESTDIR)$(libdir); \
		$(SOFTLINK) -f libspread.$(LIBFULLVERSION).dylib libspread.dylib; \
		$(SOFTLINK) -f libspread.$(LIBFULLVERSION).dylib libspread.$(LIBVERSION).dylib; \
		install_name_tool -id $(DESTDIR)$(libdir)/libspread.$(LIBVERSION).dylib $(DESTDIR)$(libdir)/libspread.$(LIBFULLVERSION).dylib \
	)

libspread.so:	$(LIBTFL_SHOBJS) $(LIBSPREADUTIL_DIR)/lib/libspread-util.sa
	$(SHLD) -o $@ $(LIBTFL_SHOBJS) $(SHLDFLAGS) $(SHARED_STDUTIL) $(SHLDCONVERTSTATIC) $(LIBSPREADUTIL_DIR)/lib/libspread-util.sa $(SHLDCONVERTSTATICEND) $(SHLIBS) $(THLIBS) $(SHLDOPTION)$(SHLDNAME)$@.$(LIBVERSION)

install-libspread.so:	libspread.so install-files
	$(INSTALL) -m 0755 libspread.so $(DESTDIR)$(libdir)/libspread.so.$(LIBFULLVERSION)
	(cd $(DESTDIR)$(libdir); \
		$(SOFTLINK) -f libspread.so.$(LIBFULLVERSION) libspread.so; \
		$(SOFTLINK) -f libspread.so.$(LIBFULLVERSION) libspread.so.$(LIBVERSION); \
	)

clean:
	rm -f *.lo *.tlo *.to *.o *.a *.dylib *.so $(TARGETS)
	rm -f core
	rm -rf autom4te.cache
	rm -rf ../lib/$(host)

distclean: clean
	rm -f Makefile config.status *~

binrelease: $(TARGETS)
	$(buildtoolsdir)/mkinstalldirs ../lib/$(host)
	$(INSTALL) -m 644 libspread.a ../lib/$(host)/libspread.a
	$(INSTALL) -m 644 libspread-core.a ../lib/$(host)/libspread-core.a
	$(INSTALL) -m 644 libtspread-core.a ../lib/$(host)/libtspread-core.a
ifeq (libspread.dylib, libspread.so)
	  $(INSTALL) -m 755 libspread.so ../lib/$(host)/libspread.so.$(LIBFULLVERSION)
	  $(INSTALL) -m 755 libspread-core.so ../lib/$(host)/libspread-core.so.$(LIBFULLVERSION)
	  $(INSTALL) -m 755 libtspread-core.so ../lib/$(host)/libtspread-core.so.$(LIBFULLVERSION)
endif
ifeq (libspread.dylib, libspread.dylib)
	  $(INSTALL) -m 755 libspread.$(LIBFULLVERSION).dylib ../lib/$(host)/libspread.$(LIBFULLVERSION).dylib
	  $(INSTALL) -m 755 libspread-core.$(LIBFULLVERSION).dylib ../lib/$(host)/libspread-core.$(LIBFULLVERSION).dylib
	  $(INSTALL) -m 755 libtspread-core.$(LIBFULLVERSION).dylib ../lib/$(host)/libtspread-core.$(LIBFULLVERSION).dylib
endif

install: $(TARGETS) install-files install-libspread.dylib install-libspread-core.dylib install-libtspread-core.dylib

install-files:
	$(buildtoolsdir)/mkinstalldirs $(DESTDIR)$(libdir)
	$(buildtoolsdir)/mkinstalldirs $(DESTDIR)$(includedir)
	$(INSTALL) -m 644 libspread.a $(DESTDIR)$(libdir)/libspread.a
	$(INSTALL) -m 644 libspread-core.a $(DESTDIR)$(libdir)/libspread-core.a
	$(INSTALL) -m 644 libtspread-core.a $(DESTDIR)$(libdir)/libtspread-core.a
	$(INSTALL) -m 644 $(top_srcdir)/include/sp.h $(DESTDIR)$(includedir)/sp.h
	$(INSTALL) -m 644 $(top_srcdir)/include/sp_func.h $(DESTDIR)$(includedir)/sp_func.h
	$(INSTALL) -m 644 $(top_srcdir)/include/sp_events.h $(DESTDIR)$(includedir)/sp_events.h
	$(INSTALL) -m 644 $(top_srcdir)/include/fl.h $(DESTDIR)$(includedir)/fl.h

uninstallall:	uninstall
	-rmdir $(DESTDIR)$(libdir)

uninstall: 
	-rm -f $(DESTDIR)$(libdir)/libspread.a
	-rm -f $(DESTDIR)$(libdir)/libspread-core.a
	-rm -f $(DESTDIR)$(libdir)/libtspread-core.a
	-rm -f $(DESTDIR)$(libdir)/libspread.dylib
	-rm -f $(DESTDIR)$(libdir)/libspread-core.dylib
	-rm -f $(DESTDIR)$(libdir)/libtspread-core.dylib
	-rm -f $(DESTDIR)$(includedir)/sp.h
	-rm -f $(DESTDIR)$(includedir)/sp_func.h
	-rm -f $(DESTDIR)$(includedir)/sp_events.h
	-rm -f $(DESTDIR)$(includedir)/fl.h
