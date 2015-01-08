import getopt
import os.path
import sys
from compression import Compressor
from zlib import error


def main(argv):
    input = ''
    output = ''
    compress = False

    try:
        opts, args = getopt.getopt(argv, 'ci:o:', ['ifile=', 'ofile='])
    except getopt.GetoptError:
        print_help()
        sys.exit(2)

    for opt, arg in opts:
        if opt in ('-i', '--ifile'):
            input = arg
        elif opt in ('-o', '--ofile'):
            output = arg
        elif opt in ('-c'):
            compress = True

    if not os.path.isfile(input):
        print('Error: Input file does not exists.')
    elif output == '':
        print('Error: You did not specify an output file.')
    else:
        file = open(output, 'wb')
        c = Compressor()

        try:
            data = c.compress_file(input) if compress \
                else c.decompress_file(input)
            file.write(data)

            print('Done! Output:', output)
        except IOError as ioe:
            print('IOError:', ioe)
        except error as ze:
            print('zlib.error:', ze)
        finally:
            file.close()


def print_help():
    print('Usage: python3 single.py -i <input> -o <output>')


if __name__ == '__main__':
    if len(sys.argv) < 1:
        print_help()
        sys.exit(2)

    main(sys.argv[1:])
