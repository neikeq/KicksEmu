import os
import sys
from compression import Compressor
from zlib import error


def main(argv):
    if argv > 1:
        analyze_all(argv[1], argv[0] == 'c')
    else:
        analyze_all(argv[0])


def analyze_all(path, compress=False):
    if not path.endswith('/'):
        path += '/'

    c = Compressor()

    for root, directory, files in os.walk(path):
        for filename in files:
            if filename.endswith(('.koa', '.koc', '.kom')):
                # create directory to save the result
                dirs = root.replace(path, '', 1)
                if not os.path.exists(dirs):
                    os.makedirs(dirs)

                # get the full path to the file
                file = os.path.join(root, filename)

                try:
                    # change the extension of the new file
                    output = remove_extension(file).replace(path, '', 1)
                    output += '.nif' if extension(file) != '.koc' else ''

                    # compress/decompress the file and save the result
                    result = c.compress_file(file) if compress \
                        else c.decompress(file)
                    f = open(output, 'wb')
                    f.write(result)
                    f.close()

                    print('Decompressed:', output)
                except error as e:
                    print('zlib.error for file:', file)
                    print('Message:', e)
                except IOError as ioe:
                    print('IOError:', ioe)


def remove_extension(name):
    return name[0:len(name)-4]


def extension(name):
    return name[len(name)-4:len(name)]


def print_help():
    print('Usage: python3 auto.py <input>')

if __name__ == '__main__':
    if len(sys.argv) < 1:
        print_help()
        sys.exit(2)

    main(sys.argv[1:])
