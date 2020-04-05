import sys

if sys.version[0] != "3":
    sys.stdout.write("WRONG_PYTHON_VERSION" + "\n")
    sys.stdout.flush()
    exit()

sys.stdout.write("PYTHON_VERSION_OK" + "\n")
sys.stdout.flush()

from enum import Enum


class Commands(Enum):
    EXIT = "exit"
    GET_COLUMNS = "get_columns"
    GET_ROWS = "get_rows"


def csv_loader():
    try:
        import pandas
    except ModuleNotFoundError:
        sys.stdout.write("PANDAS_NOT_FOUND" + "\n")
        sys.stdout.flush()
        return

    sys.stdout.write("PANDAS_IS_FOUND" + "\n")
    sys.stdout.flush()


    input_file = sys.stdin.readline().strip()
    delimiter = sys.stdin.readline().strip()
    df = pandas.read_csv(input_file, sep=delimiter)

    while True:
        full_command = input().split()
        command = full_command[0]
        args = full_command[1:]
        if command == Commands.GET_COLUMNS.value:
            sys.stdout.write(" ".join(map(str, df.columns)) + "\n")
            sys.stdout.flush()
        elif command == Commands.GET_ROWS.value:
            limit, offset = map(int, args)
            sys.stdout.write(str(len(df[offset:offset + limit])) + "\n")
            sys.stdout.flush()
            for row in df[offset:offset + limit].itertuples(index=False):
                for item in row:
                    sys.stdout.write(str(item) + "\n")
                    sys.stdout.write("END_OF_VALUE" + "\n")
                    sys.stdout.flush()
        elif command == Commands.EXIT.value:
            break


if __name__ == '__main__':
    csv_loader()
