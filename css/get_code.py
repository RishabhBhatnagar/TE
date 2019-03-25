import glob
from sys import argv

def get_code(directory_path, file_types):
    for type_file in file_types:
        for file_name in glob.iglob(directory_path + '**/*.'+type_file, recursive=True):
            print("="*20, file_name, "="*20)
            with open(file_name) as code:
                print(code.read())

for dirs in argv[1:]:
    get_code(dirs, ["py", "html"])
