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
            'Index,Id,Class,Name,Unknown,Gender,Level,Unknown,Unknown,Brand,Unknown,'
            'Payment,Exchange,Kash 7,Kash 30,Kash Perm,Points 7,Points 30,'
            'Points Perm,Unknown,Icon Path,NifFile1,NifFile2,NifFile3,'
            'NifFile4,NifFile5,Description'.split(',')
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
    row.append(reader.read_int())
    row.append(reader.read_string(32, encoding).partition('\x00')[0])
    row.append(reader.read_short())
    row.append(reader.read_short())
    row.append(reader.read_short())
    row.append(reader.read_short())
    row.append(reader.read_byte())
    row.append(reader.read_byte())
    row.append(reader.read_byte())
    row.append(reader.read_byte())
    row.append(reader.read_int())
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
    row.append(reader.read_string(32, encoding).partition('\x00')[0])
    row.append(reader.read_string(32, encoding).partition('\x00')[0])
    row.append(reader.read_string(32, encoding).partition('\x00')[0])
    row.append(reader.read_string(32, encoding).partition('\x00')[0])
    row.append(reader.read_string(200, encoding).partition('\x00')[0])

    return row


def from_row(row, writer):
    writer.write_int(int(row[0]))
    writer.write_int(int(row[1]))
    writer.write_int(int(row[2]))
    writer.write_string_fixed(row[3], 32, encoding)
    writer.write_short(int(row[4]))
    writer.write_short(int(row[5]))
    writer.write_short(int(row[6]))
    writer.write_short(int(row[7]))
    writer.write_byte(int(row[8]))
    writer.write_byte(int(row[9]))
    writer.write_byte(int(row[10]))
    writer.write_byte(int(row[11]))
    writer.write_int(int(row[12]))
    writer.write_int(int(row[13]))
    writer.write_int(int(row[14]))
    writer.write_int(int(row[15]))
    writer.write_zeros(8)
    writer.write_int(int(row[16]))
    writer.write_int(int(row[17]))
    writer.write_int(int(row[18]))
    writer.write_zeros(8)
    writer.write_int(int(row[19]))
    writer.write_string_fixed(row[20], 32, encoding)
    writer.write_string_fixed(row[21], 32, encoding)
    writer.write_string_fixed(row[22], 32, encoding)
    writer.write_string_fixed(row[23], 32, encoding)
    writer.write_string_fixed(row[24], 32, encoding)
    writer.write_string_fixed(row[25], 32, encoding)
    writer.write_string_fixed(row[26], 200, encoding)


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
    print('python3 table_item.py -i <input> -o <output>')
    print(' -b specifies that the input is a binary file')


encoding = 'windows-1252'

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print_help()
        sys.exit(2)

    main(sys.argv[1:])
