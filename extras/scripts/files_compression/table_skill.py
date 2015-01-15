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

        csv_writer.writerow(
            'Index,Id,Position,Class,Level,Unknown,Kash 7,Kash 30,'
            'Kash Perm,Points 7,Points 30,Points Perm,Unknown,Name,'
            'Icon Path,Shop Description,Description'.split(',')
        )

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
    row.append(reader.read_short())
    row.append(reader.read_short())
    row.append(reader.read_short())
    row.append(reader.read_short())
    row.append(reader.read_int())
    row.append(reader.read_int())
    row.append(reader.read_int())
    reader.ignore_bytes(8)
    row.append(reader.read_int())
    row.append(reader.read_int())
    row.append(reader.read_int())
    reader.ignore_bytes(8)
    row.append(reader.read_int())
    row.append(reader.read_string(32, encoding).partition('\x00')[0])
    row.append(reader.read_string(32, encoding).partition('\x00')[0])
    row.append(reader.read_string(200, encoding).partition('\x00')[0])
    row.append(reader.read_string(200, encoding).partition('\x00')[0])

    return row


def from_row(row, writer):
    writer.write_int(int(row[0]))
    writer.write_int(int(row[1]))
    writer.write_short(int(row[2]))
    writer.write_short(int(row[3]))
    writer.write_short(int(row[4]))
    writer.write_short(int(row[5]))
    writer.write_int(int(row[6]))
    writer.write_int(int(row[7]))
    writer.write_int(int(row[8]))
    writer.write_zeros(8)
    writer.write_int(int(row[9]))
    writer.write_int(int(row[10]))
    writer.write_int(int(row[11]))
    writer.write_zeros(8)
    writer.write_int(int(row[12]))
    writer.write_string_fixed(row[13], 32, encoding)
    writer.write_string_fixed(row[14], 32, encoding)
    writer.write_string_fixed(row[15], 200, encoding)
    writer.write_string_fixed(row[16], 200, encoding)


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
    print('python3 table_skill.py -i <input> -o <output>')
    print(' -b specifies that the input is a binary file')


encoding = 'windows-1252'

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print_help()
        sys.exit(2)

    main(sys.argv[1:])
