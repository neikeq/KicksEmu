import getopt
import os.path
import sys
from compression import Compressor
from zlib import error


def main(argv):
    finput = ''
    foutput = ''
    compress = False

    try:
        opts, args = getopt.getopt(argv, 'ci:o:', ['ifile=', 'ofile='])
    except getopt.GetoptError:
        print_help()
        sys.exit(2)

    for opt, arg in opts:
        if opt in ('-i', '--ifile'):
            finput = arg
        elif opt in ('-o', '--ofile'):
            foutput = arg
        elif opt in '-c':
            compress = True

    if not os.path.isfile(finput):
        print('Error: Input file does not exists.')
    elif foutput == '':
        print('Error: You did not specify an output file.')
    else:
        file = open(foutput, 'wb')
        c = Compressor()

        try:
            data = c.compress_file(finput) if compress \
                else c.decompress_file(finput)
            file.write(data)

            print('Done! Output:', foutput)
        except IOError as ioe:
            print('IOError:', ioe)
        except error as ze:
            print('zlib.error:', ze)
        finally:
            file.close()


def print_help():
    print('Usage:')
    print('python3 single.py -i <input> -o <output>')
    print(' -c specifies that the file must be compressed instead of decompressed')


if __name__ == '__main__':
    if len(sys.argv) < 2:
        print_help()
        sys.exit(2)

    main(sys.argv[1:])
