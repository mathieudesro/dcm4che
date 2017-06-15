    usage: dcmmrssub [<options>] <dicom-file>
    
    The dcmmrssub utility dumps the contents of a DICOM file (file format or raw
    data set) to standard output in textual form.
    -
    Options:
     -h,--help          display this help and exit
     -V,--version       output version information and exit
     -w,--width <col>   set line length; default: 78
    Example:
    $ dcmmrssub image.dcm
    Dump DICOM file image.dcm to standard output
