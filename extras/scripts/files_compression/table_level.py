import csv
import getopt
import os.path
import sys
from binary.breader import BinaryReader
from binary.bwriter import BinaryWriter


def to_table(data, path):
    with open(path, 'w') as f:
        csv_writer = csv.writer(f, delimiter=',', quotechar='"',
                                quoting=csv.QUOTE_MINIMAL)

        csv_writer.writerow('Level From,Level To,Experience,Experience Gap,UnknownFlag,Details'.split(','))

        reader = BinaryReader(data, 'little')
        size = reader.read_int()

        for i in range(size):
            csv_writer.writerow(to_row(reader))


def from_table(path):
    writer = BinaryWriter('little')

    with open(path, 'r') as csv_file:
        # Calculate the number of rows
        len_reader = csv.reader(csv_file, delimiter=',', quotechar='"')
        next(len_reader)
        length = 0
        for _ in len_reader:
            length += 1

        # Write the header which specified the number of rows
        writer.write_int(length)

        csv_file.seek(0)

        # Parse the table
        csv_reader = csv.reader(csv_file, delimiter=',', quotechar='"')
        next(csv_reader)
        for row in csv_reader:
            from_row(row, writer)

    return writer.array()


def to_row(reader):
    row = []
    row.append(reader.read_int())
    row.append(reader.read_int())
    row.append(reader.read_int())
    row.append(reader.read_int())
    row.append(reader.read_bool())
    reader.ignore_bytes(3)
    row.append(reader.read_string(32, encoding).partition('\x00')[0])

    return row


def from_row(row, writer):
    writer.write_int(int(row[0]))
    writer.write_int(int(row[1]))
    writer.write_int(int(row[2]))
    writer.write_int(int(row[3]))
    writer.write_bool(bool(row[4]))
    writer.write_zeros(3)
    writer.write_string_fixed(row[5], 32, encoding)


def main(argv):
    foutput = ''
    finput = ''
    binary = False

    try:
        opts, args = getopt.getopt(argv, 'bi:o:', ['ifile=', 'ofile='])
    except getopt.GetoptError:
        print_help()
        sys.exit(2)

    for opt, arg in opts:
        if opt in ('-i', '--ifile'):
            finput = arg
        elif opt in ('-o', '--ofile'):
            foutput = arg
        elif opt in '-b':
            binary = True

    if not os.path.isfile(finput):
        print('Error: Input file does not exists.')
    elif foutput == '':
        print('Error: You did not specify an output file.')
    else:
        if binary:
            with open(foutput, 'wb') as f:
                f.write(from_table(finput))
        else:
            with open(finput, 'rb') as f:
                to_table(f.read(), foutput)



def print_help():
    print('Usage:')
    print('python3 table_level.py -i <input> -o <output>')
    print(' -b specifies that the input is a binary file')


encoding = 'windows-1252'

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print_help()
        sys.exit(2)

    main(sys.argv[1:])
